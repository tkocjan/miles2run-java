package org.miles2run.views.views;

import org.apache.commons.lang3.StringUtils;
import org.jug.view.View;
import org.miles2run.core.repositories.jpa.SocialConnectionRepository;
import org.miles2run.domain.entities.SocialConnection;
import org.miles2run.domain.entities.SocialConnectionBuilder;
import org.miles2run.domain.entities.SocialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

@Path("/")
public class TwitterCallbackView {

    private Logger logger = LoggerFactory.getLogger(TwitterCallbackView.class);

    @Inject
    private TwitterFactory twitterFactory;
    @Inject
    private SocialConnectionRepository socialConnectionRepository;
    @Context
    private HttpServletRequest request;

    @Path("/twitter/callback")
    @GET
    @Produces("text/html")
    public View callback(@QueryParam("oauth_token") String oauthToken, @QueryParam("oauth_verifier") String oauthVerifier, @QueryParam("denied") String deniedCode) throws Exception {
        if (StringUtils.isNotBlank(deniedCode)) {
            return View.of("/", true);
        }
        RequestToken requestToken = new RequestToken(oauthToken, oauthVerifier);
        Twitter twitter = twitterFactory.getInstance();
        AccessToken oAuthAccessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
        logger.info("OAuthAccessToken : {} Token :{} TokenSecret : {} ", oAuthAccessToken, oAuthAccessToken.getToken(), oAuthAccessToken.getTokenSecret());
        String connectionId = String.valueOf(twitter.getId());
        SocialConnection existingSocialConnection = socialConnectionRepository.findByConnectionId(connectionId);
        logger.info("SocialConnection " + existingSocialConnection);
        if (existingSocialConnection != null) {
            if (isTokenAndSecretEqual(oAuthAccessToken, existingSocialConnection)) {
                logger.info("Request token and database stored in token are equal.");
                return getRedirectView(connectionId, existingSocialConnection);
            } else {
                logger.info("Request token and token stored in database are not equal. So updating database with new token.");
                existingSocialConnection.setAccessSecret(oAuthAccessToken.getToken());
                existingSocialConnection.setAccessSecret(oAuthAccessToken.getTokenSecret());
                socialConnectionRepository.save(existingSocialConnection);
                return getRedirectView(connectionId, socialConnectionRepository.findByConnectionId(connectionId));
            }
        }
        SocialConnection socialConnection = new SocialConnectionBuilder().setAccessToken(oAuthAccessToken.getToken()).setAccessSecret(oAuthAccessToken.getTokenSecret()).setProvider(SocialProvider.TWITTER).setHandle(oAuthAccessToken.getScreenName()).setConnectionId(connectionId).createSocialConnection();
        socialConnectionRepository.save(socialConnection);
        logger.info("Saved new SocialConnection with id {}", connectionId);
        return View.of("/users/new?connectionId=" + connectionId, true);
    }

    private boolean isTokenAndSecretEqual(AccessToken oAuthAccessToken, SocialConnection existingSocialConnection) {
        return StringUtils.equals(oAuthAccessToken.getToken(), existingSocialConnection.getAccessToken()) && StringUtils.equals(oAuthAccessToken.getTokenSecret(), existingSocialConnection.getAccessSecret());
    }

    View getRedirectView(String connectionId, SocialConnection existingSocialConnection) {
        if (existingSocialConnection.getProfile() == null) {
            logger.info("Profile was null. So redirecting to new profile creation.");
            return View.of("/users/new?connectionId=" + connectionId, true);
        } else {
            String username = existingSocialConnection.getProfile().getUsername();
            logger.info("User {} already had authenticated with twitter. So redirecting to home.", username);
            HttpSession session = request.getSession();
            logger.info("Using Session with id {}", session.getId());
            session.setAttribute("principal", username);
            return View.of("/", true);
        }
    }
}
