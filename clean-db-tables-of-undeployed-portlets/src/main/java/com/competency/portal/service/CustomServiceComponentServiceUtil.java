package com.competency.portal.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;


public class CustomServiceComponentServiceUtil {
	
	public static void cleanDBTablesEntriesOfUndeployedPortlets(long companyId,long userId) throws SystemException {
		//getting all sites of present portal instance
		try {
			List<Group> currentSites =  getCurrentSites(companyId);
			
			System.out.println("total "+currentSites.size()+" sites are available in current instance");
			boolean privateLayout = Boolean.FALSE;
			int count = 0;
			for (Group group : currentSites) {
				//clean all undeployed portlets entries from database tables of all public layouts of respective site
				count = cleanDBTablesEntriesOfUndeployedPortlets(group,userId,Boolean.FALSE);
				_log.info("total number of undeployed portlets is "+count+" for "+group.getName()+" site public pages");
				//clean all undeployed portlets entries from database tables of all private layouts of respective site
				privateLayout = Boolean.TRUE;
				count = cleanDBTablesEntriesOfUndeployedPortlets(group,userId,Boolean.TRUE);
				_log.info("total number of undeployed portlets is "+count+" for "+group.getName()+" site private pages");
			}
		} catch (Exception e) {
			_log.error(e, e);
		}
	}
	private static int cleanDBTablesEntriesOfUndeployedPortlets(Group group, long userId, boolean privateLayout) {
		int count = 0;
		try {
			List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(group.getGroupId(), privateLayout);
			if (_log.isInfoEnabled()) {
				_log.info("total number of applied layouts are "+layouts.size()+" for "+group.getName()+" site");
			}
			for(Layout layout : layouts) {
				//getting layouttypeportlet 
			    LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
			    //getting all portlets of respective layoutypeportlet
				List<Portlet> layoutPortlets = layoutTypePortlet.getPortlets();
				if (_log.isInfoEnabled()) {
					_log.info("total number of added portlets are "+layoutPortlets.size());
				}
				//get undeployed portlets by pas
				for(Portlet pagePortlet : layoutPortlets) {
				    if(pagePortlet.isUndeployedPortlet()) {
				    	//clean undeployed portlet entries from respective database tables  
			    		layoutTypePortlet.removePortletId(userId, pagePortlet.getPortletId());
			    		_log.info("cleaned undeployed "+pagePortlet.getPortletId()+" portlet entries from database table ");
			    		count++;
				    }
				    //update layout with latest changes
				    LayoutLocalServiceUtil.updateLayout(layout.getGroupId(), layout.isPrivateLayout(),layout.getLayoutId(), layout.getTypeSettings());
				}
			}
		} catch (SystemException e) {
			_log.error(e, e);
		} catch (PortalException e) {
			_log.error(e, e);
		}
		return count;
	}
	private static List<Group> getCurrentSites(long companyId) {
		List<Group> currentSites = new ArrayList<Group>();
		try {
			currentSites = GroupLocalServiceUtil.getGroups(companyId, Group.class.getName(), GroupConstants.DEFAULT_PARENT_GROUP_ID);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentSites;
	}
	private static Log _log = LogFactoryUtil.getLog(CustomServiceComponentServiceUtil.class);
}