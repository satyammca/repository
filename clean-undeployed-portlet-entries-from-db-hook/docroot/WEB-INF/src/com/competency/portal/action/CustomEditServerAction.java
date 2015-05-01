package com.competency.portal.action;

import com.competency.portal.service.CustomServiceComponentServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.BaseStrutsPortletAction;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

public class CustomEditServerAction extends BaseStrutsPortletAction{
	Log _log = 	LogFactoryUtil.getLog(CustomEditServerAction.class.getName());

	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		_log.info("Start : CustomEditServerAction.processAction()");
		try {
			
			originalStrutsPortletAction.processAction(originalStrutsPortletAction,portletConfig, actionRequest, actionResponse);
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
			long companyId = PortalUtil.getCompanyId(actionRequest);
			long userId = PortalUtil.getUserId(actionRequest);
			if (cmd.equals("cleanUnDeployedPortletsEntries")) {
				CustomServiceComponentServiceUtil.cleanDBTablesEntriesOfUndeployedPortlets(companyId, userId);
				SessionMessages.add(actionRequest, "clean-updeployed-portlets-success");
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_log.info("End : CustomEditServerAction.processAction()");
	}
}
