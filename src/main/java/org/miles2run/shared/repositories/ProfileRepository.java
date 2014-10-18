package org.miles2run.shared.repositories;

import org.miles2run.business.domain.jpa.Profile;
import org.miles2run.business.domain.jpa.SocialProvider;
import org.miles2run.business.vo.ProfileDetails;
import org.miles2run.business.vo.ProfileSocialConnectionDetails;
import org.miles2run.shared.exceptions.NoUserExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProfileRepository {

    private final Logger logger = LoggerFactory.getLogger(ProfileRepository.class);

    @Inject
    EntityManager entityManager;

    public Profile save(Profile profile) {
        entityManager.persist(profile);
        return profile;
    }

    public Profile findProfile(String username) {
        try {
            return entityManager.createQuery("select p from Profile p where p.username =:username", Profile.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            logger.warn("No user found with username: {}", username);
            throw new NoUserExistsException(username);
        }
    }

    public Profile findProfileByUsername(String username) {
        TypedQuery<Profile> query = entityManager.createNamedQuery("Profile.findByUsername", Profile.class);
        query.setParameter("username", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("No user found with username: {}", username);
            throw new NoUserExistsException(username);
        }
    }

    public Profile findProfileByEmail(String email) {
        TypedQuery<Profile> query = entityManager.createNamedQuery("Profile.findByEmail", Profile.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("No user found with email: {}", email);
            throw new NoUserExistsException(email);
        }
    }

    public ProfileSocialConnectionDetails findProfileWithSocialConnections(String username) {
        Query query = entityManager.createNamedQuery("Profile.findProfileWithSocialNetworks").setParameter("username", username);
        List result = query.getResultList();
        if (result == null || result.isEmpty()) {
            return null;
        }
        ProfileSocialConnectionDetails profileSocialConnectionDetails = new ProfileSocialConnectionDetails();
        result.stream().filter(object -> object instanceof Object[]).forEach(object -> {
            Object[] row = (Object[]) object;
            profileSocialConnectionDetails.setId((Long) row[0]);
            profileSocialConnectionDetails.setUsername((String) row[1]);
            profileSocialConnectionDetails.getProviders().add(((SocialProvider) row[2]).getProvider());
        });
        return profileSocialConnectionDetails;
    }

    public List<ProfileDetails> findAllProfiles(List<String> usernames) {
        return entityManager.createQuery("SELECT new org.miles2run.business.vo.ProfileDetails(p.username,p.fullname,p.profilePic, p.city, p.country,p.bio) from Profile p WHERE p.username IN :usernames", ProfileDetails.class).setParameter("usernames", usernames).getResultList();
    }

    public List<ProfileDetails> findProfileWithFullnameLike(String name) {
        return entityManager.createQuery("SELECT new org.miles2run.business.vo.ProfileDetails(p.username,p.fullname,p.profilePic, p.city, p.country) from Profile p WHERE p.fullname LIKE :name", ProfileDetails.class).setParameter("name", "%" + name + "%").getResultList();
    }

    public void update(String username, Profile profileForm) {
        Query updateQuery = entityManager.createQuery("UPDATE Profile p SET p.fullname =:fullname, p.bio =:bio,p.city =:city, p.country =:country, p.gender =:gender WHERE p.username =:username");
        updateQuery.setParameter("fullname", profileForm.getFullname());
        updateQuery.setParameter("bio", profileForm.getBio());
        updateQuery.setParameter("city", profileForm.getCity());
        updateQuery.setParameter("country", profileForm.getCountry());
        updateQuery.setParameter("gender", profileForm.getGender());
        updateQuery.setParameter("username", username);
        updateQuery.executeUpdate();

    }
}
