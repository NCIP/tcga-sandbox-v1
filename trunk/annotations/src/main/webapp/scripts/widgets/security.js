/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/**
 * Namespace for Annotations' security.js (this file)
 */
Ext.namespace('tcga.annotations.security');

/**
 * Constants
 */
var LOGOUT_ICON_FILENAME = 'images/icons/door_in.png';
var SPRING_LOGOUT_URL = 'j_spring_security_logout';
var SPRING_LOGIN_URL = 'j_spring_security_check';
var SPRING_LOGIN_USERNAME_PARAM = 'j_username';
var SPRING_LOGIN_PASSWORD_PARAM = 'j_password';
var SECURITY_USERNAME_JSON_URL = 'username.securityjson';
var SECURITY_AUTHORITIES_JSON_URL = 'authorities.securityjson';
var SECURITY_ACL_AUTHORITIES_JSON_URL = 'acl.securityjson';

/**
 * The login name of the authenticated user
 */
var username = null;

tcga.annotations.security.getUsername = function() {
    return username;
}

tcga.annotations.security.setUsername = function(usernameArg) {
    username = usernameArg;
}

/**
 * Return the Logout button
 */
var logoutButton;
tcga.annotations.security.getLogoutButton = function() {

    if (!logoutButton) {
        logoutButton = new tcga.extensions.buttonPlus({
            id : 'logoutButton',
            baseCls: 'stdLabel',
            autoWidth: true,
            minWidth: 60,
            text: tcga.annotations.security.getUsername(),
            listeners: {
            	menushow: function(btn, menu) {
            		var m = menu.getEl();
            		m.setWidth(80);
            	}
            },
            menu: {
                items: [
                    {
                        text:'Log out',
                        icon: LOGOUT_ICON_FILENAME,
                        handler: function() {
                            tcga.annotations.security.logout();
                        }
                    }
                ]
            },
            hidden: true
        });
    }

    return logoutButton;
}

tcga.annotations.security.showLogoutButton = function() {
    Ext.getCmp('logoutButton').show();
}

tcga.annotations.security.hideLogoutButton = function() {
    Ext.getCmp('logoutButton').hide();
}

/**
 * Return the Login button
 */
var loginButton;
tcga.annotations.security.getLoginButton = function() {

    if (!loginButton) {
        loginButton = new tcga.extensions.buttonPlus({
            id : 'loginButton',
            text: 'Login',
            handler: function() {
                tcga.annotations.security.showLoginPopup();
            },
            hidden: false
        });
    }

    return loginButton;
}

tcga.annotations.security.showLoginButton = function() {
    Ext.getCmp('loginButton').show();
}

tcga.annotations.security.hideLoginButton = function() {
    Ext.getCmp('loginButton').hide();
}

/**
 * This function calls the spring security default url for logging out an authenticated user
 */
tcga.annotations.security.logout = function() {

    Ext.Ajax.request({
        url : SPRING_LOGOUT_URL,
        method: 'GET',
        success: function (result, request) {

            setMainText(defaultWelcomeMessage);
            tcga.annotations.security.showLoginButton();
            tcga.annotations.security.hideLogoutButton();

            //Reset username
            tcga.annotations.security.setUsername(null);

            //Reset user authorities
            tcga.annotations.security.setAuthenticatedPrincipalAuthorities(null);

            //Redraw restricted UI
            tcga.annotations.security.redrawRestrictedUI();
            tcga.annotations.security.redrawRoleBasedText();

            redrawSearchResults();
        },
        failure: function (result, request) {
            Ext.MessageBox.alert('Error: Could not log out!');
        }
    });
}

/**
 * The default height for the login form
 */
var defaultLoginFormHeight = 100;

/**
 * The height for the login form when an error message needs to be displayed
 */
var errorMessageLoginFormHeight = 150;

/**
 * The login form
 */
var loginForm;
tcga.annotations.security.getLoginForm = function() {

    if(!loginForm) {
        loginForm = new Ext.FormPanel({
            id:'loginForm',
            defaultType:'textfield',
            width: 325,
            height: defaultLoginFormHeight,
            bodyStyle:'padding:5px 5px 0',
            monitorValid:true,
            frame: true,
            labelWidth: 75,
            defaults: {width: 200},
            items:[
                {
                    id:SPRING_LOGIN_USERNAME_PARAM,
                    fieldLabel:'Username',
                    name: SPRING_LOGIN_USERNAME_PARAM,
                    allowBlank:false
                },
                {
                    id:SPRING_LOGIN_PASSWORD_PARAM,
                    fieldLabel:'Password',
                    name: SPRING_LOGIN_PASSWORD_PARAM,
                    inputType:'password',
                    allowBlank:false
                },
                {
                    id: 'errorField',
                    xtype: 'label',
                    width: 310,
                    style:'display:block;height:30px;padding:10px 0px 0px 0px;color:red',
                    html: '',
                    hidden: true
                }
            ],
            buttons: [
                {
                    id: 'loginWinLoginButton',
                    xtype: 'buttonplus',
                    text: 'Login',
                    handler: function() {
                      if(loginForm.getForm().isValid()){
                        Ext.Ajax.request({
                            url: SPRING_LOGIN_URL,
                            params: {
                                j_username: Ext.get(SPRING_LOGIN_USERNAME_PARAM).getValue(),
                                j_password: Ext.get(SPRING_LOGIN_PASSWORD_PARAM).getValue()
                            },
                            method: 'POST',
                            success: function (result) {
                                var res = result.responseText;
                                if (res.indexOf('Authentication failed.') > 0) {
                                    tcga.annotations.security.showErrorMsg(401);
                                } else {
                                    tcga.annotations.security.hideLoginPopup();
                                    tcga.annotations.security.updateUIWithUserNameFromServer();
                                }
                            },
                            failure: function (result) {
                                tcga.annotations.security.showErrorMsg(result.status);
                            }
                        });
                      }
                    }
                },
                {
                    id: 'loginWinResetButton',
                    xtype: 'buttonplus',
                    text: 'Reset',
                    handler: function() {
                        tcga.annotations.security.hideErrorField();
                        loginForm.getForm().reset();
                    }
                },
                {
                    id: 'loginWinCancelButton',
                    xtype: 'buttonplus',
                    text: 'Cancel',
                    handler: function() {
                        tcga.annotations.security.hideErrorField();
                        loginForm.getForm().reset();
                        loginWindow.hide();
                    }
                }
            ]
        });
    }

    return loginForm;
}

/**
 * Display an error message on the login window according to http status code received.
 * Note: only 401 has been made human readable at this point, but more code can be added.
 *
 * @param httpStatusCode
 */
tcga.annotations.security.showErrorMsg = function(httpStatusCode) {

    var errorText = 'Error: ';

    switch (httpStatusCode) {
        case 0:
            tcga.annotations.security.setErrorField(errorText + 'No Reponse (The connection may be down)');
            break;
        case 401:
            tcga.annotations.security.setErrorField(errorText + 'The username or password entered is incorrect.');
            break;
        default:
            tcga.annotations.security.setErrorField(errorText + 'HTTP status code ' + httpStatusCode);
    }

    tcga.annotations.security.showErrorField();
}

/**
 * Set the text in the error field of the login form
 *
 * @param text
 */
tcga.annotations.security.setErrorField = function(text) {
    Ext.getCmp('errorField').update(text);
}

/**
 * Show Error field on login form
 */
tcga.annotations.security.showErrorField = function() {

    tcga.annotations.security.setLoginFormHeight(errorMessageLoginFormHeight);
    Ext.getCmp('errorField').show();
}

/**
 * Hide the error field from the login form
 */
tcga.annotations.security.hideErrorField = function() {

    tcga.annotations.security.setLoginFormHeight(defaultLoginFormHeight);
    Ext.getCmp('errorField').hide();
}

/**
 * Sets the height of the login form
 *
 * @param height the height to set
 */
tcga.annotations.security.setLoginFormHeight = function(height) {
    Ext.getCmp('loginForm').setHeight(height);
}

/**
 * Show the popup to login with the given error message, if any
 *
 * @param errorMessage the error message to display, if any
 */
var loginWindow;
tcga.annotations.security.showLoginPopup = function(errorMessage) {

    if (!loginWindow) {
        loginWindow = new Ext.Window({
            id: 'loginWindow',
            layout:'fit',
            modal: true,
            width: 350,
            onEsc: function() {
                loginWindow.hide();
            },
            title: 'Login',
            closeAction: 'hide',
            items: [
                tcga.annotations.security.getLoginForm()
            ]
        });
    }

    tcga.annotations.security.showLoginErrorMessage(errorMessage)

    loginWindow.show();
    loginForm.get(SPRING_LOGIN_USERNAME_PARAM).focus(false, 500);
}

/**
 * Show the given error message on the login form
 *
 * @param errorMessage the error message to display, if any
 */
tcga.annotations.security.showLoginErrorMessage = function(errorMessage) {

    if(errorMessage) {
        tcga.annotations.security.setErrorField(errorMessage);
        tcga.annotations.security.showErrorField();
    }
}

tcga.annotations.security.hideLoginPopup = function() {

    loginWindow.hide();
    loginForm.getForm().reset();
    tcga.annotations.security.hideErrorField();
}

/**
 * Retrieves the authenticated principal username value from the server,
 * update the local username value, and update the UI
 */
tcga.annotations.security.updateUIWithUserNameFromServer = function() {

    var defaultUsernameIfFailure = 'username could not be retrieved';

    Ext.Ajax.request({
        url: SECURITY_USERNAME_JSON_URL,
        method: 'GET',
        success: function (result, request) {
            var response = Ext.util.JSON.decode(result.responseText);
            if(response.authenticatedPrincipalUsername != "not_authenticated") {
                tcga.annotations.security.setUsername(response.authenticatedPrincipalUsername);
                tcga.annotations.security.updateUIWithUserAuthenticated();
            }
        },
        failure: function (result, request) {
            tcga.annotations.security.updateLogoutText(defaultUsernameIfFailure);
        }
    });
}

/**
 * Update the UI if the user is authenticated
 */
tcga.annotations.security.updateUIWithUserAuthenticated = function() {

    if(tcga.annotations.security.getUsername() != null) {

        tcga.annotations.security.updateLogoutText(tcga.annotations.security.getUsername());
        tcga.annotations.security.showLogoutButton();
        tcga.annotations.security.hideLoginButton();
        tcga.annotations.security.updateUIWithAuthoritiesFromServer();
        redrawSearchResults();
    }
}

/**
 * Update the username value on the Logout button
 *
 * @param text
 */
tcga.annotations.security.updateLogoutText = function(text) {
    Ext.getCmp('logoutButton').setText(text);
}

/**
 * Retrieve the authenticated user's authorities and redraw the UI based on the ROLE-based restrictions
 * applying to the user
 */
tcga.annotations.security.updateUIWithAuthoritiesFromServer = function() {

    var defaultAuthoritiesIfFailure = null;

    Ext.Ajax.request({
        url: SECURITY_AUTHORITIES_JSON_URL,
        method: 'GET',
        success: function (result, request) {
            var response = Ext.util.JSON.decode(result.responseText);
            tcga.annotations.security.setAuthenticatedPrincipalAuthorities(response.authenticatedPrincipalAuthorities);
            tcga.annotations.security.redrawRestrictedUI();
            tcga.annotations.security.redrawRoleBasedText();
        },
        failure: function (result, request) {
            tcga.annotations.security.setAuthenticatedPrincipalAuthorities(defaultAuthoritiesIfFailure);
        }
    });
}

/**
 * Retrieve the ACL-based permission of the authenticated user for a given object instance and redraw the UI
 * based on the user's restrictions for that object
 */
tcga.annotations.security.updateUIWithACLAuthoritiesFromServer = function(aclVoterName, objectClazz, objectId, domId) {

    if(tcga.annotations.security.getUsername() != null && aclVoterName != null && objectClazz != null && objectId != null && domId != null) {

        Ext.Ajax.request({
            url: SECURITY_ACL_AUTHORITIES_JSON_URL,
            params: {
                aclVoterName: aclVoterName,
                objectClass: objectClazz,
                objectId: objectId,
                recipient: tcga.annotations.security.getUsername()
            },
            method: 'GET',
            success: function (result, request) {
                var response = Ext.util.JSON.decode(result.responseText);
                if(response.hasPermission=='true') {
                    tcga.annotations.security.unrestrict(domId);
                }

            },
            failure: function (result, request) {
                Ext.MessageBox.alert("Error: Could not get ACL authorities!");
            }
        });
    }
}

/**
 * An array of ROLE-based authorities for the authenticated user
 */
var authenticatedPrincipalAuthorities;

tcga.annotations.security.setAuthenticatedPrincipalAuthorities = function(authenticatedPrincipalAuthoritiesArg) {
    authenticatedPrincipalAuthorities = authenticatedPrincipalAuthoritiesArg;
}

tcga.annotations.security.getAuthenticatedPrincipalAuthorities = function() {
    return authenticatedPrincipalAuthorities;
}

/**
 * Return true if the array contains the item
 *
 * @param array
 * @param item
 */
tcga.annotations.security.contains = function(array, item) {
  var i = array.length;
  while (i--) {
    if (array[i] === item) {
      return true;
    }
  }
  return false;
}

/**
 * Return true if the user has the given role
 *
 * @param role
 */
tcga.annotations.security.hasRequiredRole = function(role) {

    var result = false;
    if(tcga.annotations.security.getAuthenticatedPrincipalAuthorities()) {
        result = tcga.annotations.security.contains(tcga.annotations.security.getAuthenticatedPrincipalAuthorities(), role);
    }

    return result;
}

/**
 * Return true if the authenticated principal has all the roles specified in rolArray, false otherwise
 *
 * @param roleArray
 */
tcga.annotations.security.hasRequiredRoles = function(roleArray) {

    if(roleArray) {

        for(var i = 0; i < roleArray.length; i++) {

            if(tcga.annotations.security.hasRequiredRole(roleArray[i])) {
                return true;
            }
        }
    }

    return false;
}

/**
 * A collection of id of DOM elements that need to be restricted
 */
var restrictedDomIdMixedCollection;
tcga.annotations.security.getRestrictedDomIdMixedCollection = function() {

    if(!restrictedDomIdMixedCollection) {
        restrictedDomIdMixedCollection = new Ext.util.MixedCollection();
    }

    return restrictedDomIdMixedCollection;
}

/**
 * A collection of id of DOM elements that have role based text values
 */
var roleBasedTextUpdatesCollection;
tcga.annotations.security.getRoleBasedTextUpdatesCollection = function() {

    if(!roleBasedTextUpdatesCollection) {
        roleBasedTextUpdatesCollection = new Ext.util.MixedCollection();
    }

    return roleBasedTextUpdatesCollection;
}

/**
 * Add an array of ROLE restrictions and an array of ACL restrictions to the DOM element with id domId.
 * In case there are ACL restrictions, the class (clazz) and the class Id (clazzId) of the Java Object Instance
 * to protect need to be specified. Restrict boolean will hide object completely from other roles.
 *
 * @param domId
 * @param roleRestrictionArray
 * @param aclRestriction
 * @param clazz
 * @param clazzId
 * @param restrict
 */
tcga.annotations.security.addRestrictionsMixedCollectionToDomId = function(domId, roleRestrictionArray, aclRestriction, clazz, clazzId, restrict) {

    var restrictionsMixedCollection = new Ext.util.MixedCollection();
    restrictionsMixedCollection.add('ROLE', roleRestrictionArray);
    restrictionsMixedCollection.add('ACL', aclRestriction);
    restrictionsMixedCollection.add('CLAZZ', clazz);
    restrictionsMixedCollection.add('CLAZZID', clazzId);
    restrictionsMixedCollection.add('RESTRICT', restrict);

    tcga.annotations.security.getRestrictedDomIdMixedCollection().add(domId, restrictionsMixedCollection);
}

/**
 * Add an array of ROLE - based text requirements to the DOM element with id domId.
 *
 * @param domId
 * @param roleRestrictionArray
 * @param textType - type of text... i.e title or text
 * @param text - actual text required for roles
 * @param defaultText - text required for non members of the roles
 */
tcga.annotations.security.addRoleBasedTextUpdatesToDomId = function(domId, roleRestrictionArray, textType, text, defaultText) {

    var roleBasedTextUpdatesCollection = new Ext.util.MixedCollection();
    roleBasedTextUpdatesCollection.add('ROLE', roleRestrictionArray);
    roleBasedTextUpdatesCollection.add('TEXTTYPE', textType);
    roleBasedTextUpdatesCollection.add('TEXT', text);
    roleBasedTextUpdatesCollection.add('DEFAULTTEXT', defaultText);

    tcga.annotations.security.getRoleBasedTextUpdatesCollection().add(domId, roleBasedTextUpdatesCollection);
}

/**
 * For each DOM element registered as a restricted UI element, show it or hide it according to the user's authorities
 */
tcga.annotations.security.redrawRestrictedUI = function(domIdToRedrawArray) {

    if(domIdToRedrawArray) {
        //Redraw only elements provided in domIdToRedrawArray
        for(var i = 0; i < domIdToRedrawArray.length; i++) {
            var domId = domIdToRedrawArray[i];
            tcga.annotations.security.redrawDomId(domId, 'single');
        }

    } else {
        //Redraw all restricted UI elements
        for(var i = 0; i < tcga.annotations.security.getRestrictedDomIdMixedCollection().getCount(); i++) {
            var domId = tcga.annotations.security.getRestrictedDomIdMixedCollection().keys[i];
            tcga.annotations.security.redrawDomId(domId, 'all');
        }
    }
}

/**
 * Gets all the dom ids contained in getRoleBasedTextUpdatesCollection and updates any role-based text
 *
 */
tcga.annotations.security.redrawRoleBasedText = function() {
		var domId;
		var roleBasedMixedCollection;
		var textType;
		var requiredRolesArray;
		var changeText;
		var defaultText;
		
        //Redraw all restricted UI elements
        for(var i = 0; i < tcga.annotations.security.getRoleBasedTextUpdatesCollection().getCount(); i++) {
            domId = tcga.annotations.security.getRoleBasedTextUpdatesCollection().keys[i];
            roleBasedMixedCollection = tcga.annotations.security.getRoleBasedTextUpdatesCollection().get(domId);
			textType = roleBasedMixedCollection.get('TEXTTYPE');
			requiredRolesArray = roleBasedMixedCollection.get('ROLE');
			changeText = roleBasedMixedCollection.get('TEXT');  
			defaultText =  roleBasedMixedCollection.get('DEFAULTTEXT');        
            tcga.annotations.security.replaceTextUI(textType,domId,requiredRolesArray,changeText,defaultText);
        }
}

/**
 * Given the dom id and text type, this function will change text in the UI to a given value
 * if user's role is in the given array of roles otherwise it will set the text to the default.
 *
 * @param textType - text to change to if user's role is in array of roles.
 * @param domId - id of dom element upon which the change is based.
 * @param requiredRolesArray - roles required to change text
 * @param changeText - text to change to.
 * @param defaultText - default text if user'role is not in required roles.
 */
tcga.annotations.security.replaceTextUI = function(textType,domId,requiredRolesArray,changeText,defaultText) {
    if(domId && tcga.annotations.security.hasRequiredRoles(requiredRolesArray)) {
		tcga.annotations.security.setTextUI(domId, textType, changeText);
	} else {
		tcga.annotations.security.setTextUI(domId, textType, defaultText);
	}
}

/**
 * Given the dom id and text type, this function will change text in the UI to a given value
 *
 * @param textType - text to change to.
 * @param domId - id of dom element upon which the change is based.
 * @param changeText - text to change to.
 */
tcga.annotations.security.setTextUI = function(domId, textType, changeText) {
    	if(textType.toLowerCase() == "text"){
	        Ext.getCmp(domId).setText(changeText);
        }else if(textType.toLowerCase() == "title"){
	        Ext.getCmp(domId).setTitle(changeText);
        }	
}

/**
 * Redraw the DOM element with Id domId
 * @param domId
 */
tcga.annotations.security.redrawDomId = function(domId, tmp) {

    var restrictionsMixedCollection = tcga.annotations.security.getRestrictedDomIdMixedCollection().get(domId);
    if(restrictionsMixedCollection) {
        var roleArray = restrictionsMixedCollection.get('ROLE');
        var aclVoterName = restrictionsMixedCollection.get('ACL');
        var clazz = restrictionsMixedCollection.get('CLAZZ');
        var clazzId = restrictionsMixedCollection.get('CLAZZID');
        var restrict = restrictionsMixedCollection.get('RESTRICT');

        var userHasRequiredRoles = tcga.annotations.security.hasRequiredRoles(roleArray);
        if(userHasRequiredRoles) {

            if(aclVoterName && clazz && clazzId) {
                tcga.annotations.security.updateUIWithACLAuthoritiesFromServer(aclVoterName, clazz, clazzId, domId);
            } else {
                tcga.annotations.security.unrestrict(domId,restrict);
            }
        } else {
            tcga.annotations.security.restrict(domId,restrict);
        }
    }
}

/**
 * Enable or hide the DOM element with Id domId
 * @param domId
 * @param restrictVisibility - if true, restrict will hide completely
 */
tcga.annotations.security.restrict = function(domId,restrictVisibility) {

    if(Ext.getCmp(domId)) {
    	if(restrictVisibility == true)
    		Ext.getCmp(domId).setVisible(false);
        else
        	Ext.getCmp(domId).setDisabled(true);
    }
}

/**
 * Enable or hide the DOM element with Id domId
 * @param domId
 * @param restrictVisibility - if true, unrestrict will show
 */
tcga.annotations.security.unrestrict = function(domId,restrictVisibility) {
    
    if(Ext.getCmp(domId)) {
    	if(restrictVisibility == true)
        	Ext.getCmp(domId).setVisible(true);
        else
        	Ext.getCmp(domId).setDisabled(false);
    }
}