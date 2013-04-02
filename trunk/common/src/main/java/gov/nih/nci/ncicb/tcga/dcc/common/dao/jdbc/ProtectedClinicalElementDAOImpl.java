package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ProtectedClinicalElement;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ProtectedClinicalElementDAO;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link ProtectedClinicalElementDAO} data access object interface for querying
 * and performing CRUD operations for the <tt>PROTECTED_CLINICAL_ELEMENT</tt> table.
 * 
 * @author nichollsmc
 */
@Repository
@Transactional
public class ProtectedClinicalElementDAOImpl implements ProtectedClinicalElementDAO {

	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public Integer addElement(final ProtectedClinicalElement protectedClinicalElement) {
		Session session = null;
		if(protectedClinicalElement != null) {
			session = sessionFactory.getCurrentSession();
			final ProtectedClinicalElement existingProtectedClinicalElement = 
					getElementByName(session, protectedClinicalElement.getElementName());
			if(existingProtectedClinicalElement == null) {
				return (Integer) session.save(protectedClinicalElement);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public ProtectedClinicalElement getElementByName(final String elementName) {
		return getElementByName(sessionFactory.getCurrentSession(), elementName);
	}
	
	@Override
	public boolean isProtected(String elementName) {
		return getElementByName(sessionFactory.getCurrentSession(), elementName) == null ? false : true;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ProtectedClinicalElement> getElements() {
		return sessionFactory.getCurrentSession().createCriteria(ProtectedClinicalElement.class).list();
	}
	
	@Override
	public void removeElementByName(final String elementName) {
		Session session = sessionFactory.getCurrentSession();
		final ProtectedClinicalElement protectedClinicalElement = getElementByName(session, elementName);
		if(protectedClinicalElement != null) {
			session.delete(protectedClinicalElement);
		}
	}
	
	private ProtectedClinicalElement getElementByName(final Session session, final String elementName) {
		if(elementName != null && !elementName.isEmpty()) {
			return (ProtectedClinicalElement) session.createCriteria(ProtectedClinicalElement.class)
					.add(Restrictions.eq("elementName", elementName)).uniqueResult();
		}
		else {
			return null;
		}
	}
}
