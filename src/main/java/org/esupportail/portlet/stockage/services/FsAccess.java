/**
 * Copyright (C) 2010 Esup Portail http://www.esup-portail.org
 * Copyright (C) 2010 UNR RUNN http://www.unr-runn.fr
 * @Author (C) 2010 Vincent Bonamy <Vincent.Bonamy@univ-rouen.fr>
 * @Contributor (C) 2010 Jean-Pierre Tran <Jean-Pierre.Tran@univ-rouen.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.esupportail.portlet.stockage.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.esupportail.portlet.stockage.beans.DownloadFile;
import org.esupportail.portlet.stockage.beans.JsTreeFile;
import org.esupportail.portlet.stockage.beans.SharedUserPortletParameters;
import org.esupportail.portlet.stockage.beans.UserPassword;
import org.esupportail.portlet.stockage.services.auth.FormUserPasswordAuthenticatorService;
import org.esupportail.portlet.stockage.services.auth.UserAuthenticatorService;
import org.esupportail.portlet.stockage.services.uri.UriManipulateService;

public abstract class FsAccess {

	protected static String TOKEN_SPECIAL_CHAR =  "@";
	
	private List<String> memberOfAny;
	
	private String contextToken;
	
	protected String driveName;
	
	protected String uri;
	
	protected String icon;
	
	protected UserAuthenticatorService userAuthenticatorService;

	protected UriManipulateService uriManipulateService;

	public List<String> getMemberOfAny() {
		return memberOfAny;
	}

	public void setMemberOfAny(List<String> memberOfAny) {
		this.memberOfAny = memberOfAny;
	}

	public String getContextToken() {
		return contextToken;
	}

	public void setContextToken(String contextToken) {
		this.contextToken = contextToken;
	}

	public String getDriveName() {
		return driveName;
	}

	public void setDriveName(String driveName) {
		this.driveName = driveName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setUserAuthenticatorService(
			UserAuthenticatorService userAuthenticatorService) {
		this.userAuthenticatorService = userAuthenticatorService;
	}

	public void setUriManipulateService(
			UriManipulateService uriManipulateService) {
		this.uriManipulateService = uriManipulateService;
	}
	
	public void initializeService(Map userInfos,
			SharedUserPortletParameters userParameters) {
		if(userInfos != null) {
			for(String userInfoKey : (Set<String>)userInfos.keySet()) { 
				String userInfo = (String)userInfos.get(userInfoKey);
				String userInfoKeyToken = TOKEN_SPECIAL_CHAR.concat(userInfoKey).concat(TOKEN_SPECIAL_CHAR);
				this.uri = this.uri.replaceAll(userInfoKeyToken, userInfo);
			}
		}
		if(this.userAuthenticatorService != null && userInfos != null)
			this.userAuthenticatorService.initialize(userInfos, userParameters);
		if(this.uriManipulateService != null) 
			this.uri = this.uriManipulateService.manipulate(uri);			
	}

	public abstract void open() ;

	public abstract void close();

	public abstract boolean isOpened();

	public abstract JsTreeFile get(String path) ;

	public abstract List<JsTreeFile> getChildren(String path);

	public abstract boolean remove(String path);

	public abstract String createFile(String parentPath, String title,
			String type);

	public abstract boolean renameFile(String path, String title);

	public abstract boolean moveCopyFilesIntoDirectory(String dir,
			List<String> filesToCopy, boolean copy);

	public abstract DownloadFile getFile(String dir);

	public abstract boolean putFile(String dir, String filename,
			InputStream inputStream);
	
	public boolean supportIntraCopyPast() {
		return true;
	}
	
	public boolean supportIntraCutPast() {
		return true;
	}

	public boolean formAuthenticationRequired() {
		if(this.userAuthenticatorService instanceof FormUserPasswordAuthenticatorService) {
			if(this.userAuthenticatorService.getUserPassword().getPassword() == null || this.userAuthenticatorService.getUserPassword().getPassword().length() == 0)
				return true;
		}
		return false;
	}

	public UserPassword getUserPassword() {
		return this.userAuthenticatorService.getUserPassword();
	}

	public boolean authenticate(String username, String password) {
		this.userAuthenticatorService.getUserPassword().setUsername(username);
		this.userAuthenticatorService.getUserPassword().setPassword(password);
		try { 
			this.get("");
		} catch(Exception e) {
			// TODO : catch Exception corresponding to an authentication failure ... 
			this.userAuthenticatorService.getUserPassword().setPassword(null);
			return false;
		}
		return true;
	}

}