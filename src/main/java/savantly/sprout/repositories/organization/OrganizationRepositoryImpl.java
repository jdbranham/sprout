package savantly.sprout.repositories.organization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import savantly.sprout.domain.Organization;
import savantly.sprout.domain.OrganizationMember;
import savantly.sprout.domain.OrganizationMemberId;
import savantly.sprout.domain.SproutUser;
import savantly.sprout.repositories.organization.exception.UnknownOrganizationException;
import savantly.sprout.repositories.user.exception.UnknownUserException;
import savantly.sprout.security.Permission;

public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom{
	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Override
	public Organization addMember(String organizationId, String emailAddress, Permission permission) {
		List<SproutUser> otherUserList = mongoTemplate.find(Query.query(Criteria.where("emailAddress").is(emailAddress)), SproutUser.class);
		
		SproutUser otherUser;
		
		if(otherUserList.isEmpty()) {
			throw new UnknownUserException();
		} else {
			otherUser = otherUserList.get(0);
		}

		Organization organization = mongoTemplate.findById(organizationId, Organization.class);
		if(organization == null) throw new UnknownOrganizationException();
		
		OrganizationMemberId memberId = new OrganizationMemberId(otherUser, permission);
		OrganizationMember member = new OrganizationMember(memberId);
		organization.addMember(member);

		mongoTemplate.save(organization);
		return organization;
	}

}
