/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/**
 * Namespace for UUID' security.js (this file)
 */
Ext.namespace('tcga.uuid.security');

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
var username;

tcga.uuid.security.getUsername = function() {
    return username;
}

tcga.uuid.security.setUsername = function(usernameArg) {
    username = usernameArg;
}

/**
 * Return the Logout button
 */
var logoutButton;
tcga.uuid.security.getLogoutButton = function() {
    if (!logoutButton) {
        logoutButton = new tcga.extensions.buttonPlus({
                    id : 'logoutButton',
                    baseCls: 'stdLabel',
                    autoWidth: true,
                    minWidth: 60,
                    text: tcga.uuid.security.getUsername(),
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
                                    tcga.uuid.security.logout();
                                }
                            }
                        ]
                    }
            });
    }
    return logoutButton;
}

tcga.uuid.security.showLogoutButton = function() {
    Ext.getCmp('logoutButton').show();
}

tcga.uuid.security.hideLogoutButton = function() {
    Ext.getCmp('logoutButton').hide();
}

/**
 * Return the Login button
 */

var loginButton;
tcga.uuid.security.getLoginButton = function() {
        if (!loginButton) {
            loginButton = new tcga.extensions.buttonPlus({
                        title: 'Login',
                        id : 'loginButton',
                        baseCls: 'stdLabel',
                        minWidth: 60,
                        text: 'Login',
                        hidden: false,
                        handler: showLoginPopupWrapper
                });
        }
    return loginButton;
}

var showLoginPopupWrapper = function() {
    tcga.uuid.security.showLoginPopup();
}

tcga.uuid.security.showLoginButton = function() {
    Ext.getCmp('loginButton').show();
}

tcga.uuid.security.hideLoginButton = function() {
    Ext.getCmp('loginButton').hide();
}

/**
 * This function calls the spring security default url for logging out an authenticated user
 */
tcga.uuid.security.logout = function() {

    Ext.Ajax.request({
        url : SPRING_LOGOUT_URL,
        method: 'GET',
        success: function (result, request) {

            tcga.uuid.security.showLoginButton();
            tcga.uuid.security.hideLogoutButton();

            //Reset username
            tcga.uuid.security.setUsername("");

            //Reset user authorities
            tcga.uuid.security.setAuthenticatedPrincipalAuthorities(null);

            //Redraw restricted UI
            tcga.uuid.security.redrawRestrictedUI();

            // make search tab active
            Ext.getCmp('uuidTabPanel').setActiveTab('uuidFindPanel');
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
tcga.uuid.security.getLoginForm = function() {

    if (!loginForm) {
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
                                    tcga.uuid.security.showErrorMsg(401);
                                } else {
                                    tcga.uuid.security.hideLoginPopup();
                                    tcga.uuid.security.updateUIWithUserNameFromServer();
                                }
                            },
                            failure: function (result) {
                                tcga.uuid.security.showErrorMsg(result.status);
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
                        tcga.uuid.security.hideErrorField();
                        loginForm.getForm().reset();
                    }
                },
                {
                    id: 'loginWinCancelButton',
                    xtype: 'buttonplus',
                    text: 'Cancel',
                    handler: function() {
                        tcga.uuid.security.hideErrorField();
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
tcga.uuid.security.showErrorMsg = function(httpStatusCode) {

    var errorText = 'Error: ';

    switch (httpStatusCode) {
        case 0:
            tcga.uuid.security.setErrorField(errorText + 'No Reponse (The connection may be down)');
            break;
        case 401:
            tcga.uuid.security.setErrorField(errorText + 'The username or password entered is incorrect.');
            break;
        default:
            tcga.uuid.security.setErrorField(errorText + 'HTTP status code ' + httpStatusCode);
    }

    tcga.uuid.security.showErrorField();
}

/**
 * Set the text in the error field of the login form
 *
 * @param text
 */
tcga.uuid.security.setErrorField = function(text) {
    Ext.getCmp('errorField').update(text);
}

/**
 * Show Error field on login form
 */
tcga.uuid.security.showErrorField = function() {

    tcga.uuid.security.setLoginFormHeight(errorMessageLoginFormHeight);
    Ext.getCmp('errorField').show();
}

/**
 * Hide the error field from the login form
 */
tcga.uuid.security.hideErrorField = function() {
    
    tcga.uuid.security.setLoginFormHeight(defaultLoginFormHeight);
    Ext.getCmp('errorField').hide();
}

/**
 * Sets the height of the login form
 *
 * @param height the height to set
 */
tcga.uuid.security.setLoginFormHeight = function(height) {
    Ext.getCmp('loginForm').setHeight(height);
}

/**
 * Show the popup to login with the given error message, if any
 *
 * @param errorMessage the error message to display, if any
 */
var loginWindow;
tcga.uuid.security.showLoginPopup = function(errorMessage) {
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
                tcga.uuid.security.getLoginForm()
            ]
        });
    }

    tcga.uuid.security.showLoginErrorMessage(errorMessage);

    loginWindow.show();
    loginForm.get(SPRING_LOGIN_USERNAME_PARAM).focus(false, 500);
}

/**
 * Show the given error message on the login form
 *
 * @param errorMessage the error message to display, if any
 */
tcga.uuid.security.showLoginErrorMessage = function(errorMessage) {

    if(errorMessage) {
        tcga.uuid.security.setErrorField(errorMessage);
        tcga.uuid.security.showErrorField();
    }
}

tcga.uuid.security.hideLoginPopup = function() {

    loginWindow.hide();
    loginForm.getForm().reset();
    tcga.uuid.security.hideErrorField();
}

/**
 * Retrieves the authenticated principal username value from the server,
 * update the local username value, and update the UI
 */
tcga.uuid.security.updateUIWithUserNameFromServer = function() {

    var defaultUsernameIfFailure = 'username could not be retrieved';

    Ext.Ajax.request({
        url: SECURITY_USERNAME_JSON_URL,
        method: 'GET',
        success: function (result, request) {
            var response = Ext.util.JSON.decode(result.responseText);
            if(response.authenticatedPrincipalUsername != "not_authenticated") {
                tcga.uuid.security.setUsername(response.authenticatedPrincipalUsername);
                tcga.uuid.security.updateUIWithUserAuthenticated();
            }
        },
        failure: function (result, request) {
            tcga.uuid.security.updateLogoutText(defaultUsernameIfFailure);
        }
    });
}

/**
 * Update the UI if the user is authenticated
 */
tcga.uuid.security.updateUIWithUserAuthenticated = function() {

    if(tcga.uuid.security.getUsername() != null) {
        
        tcga.uuid.security.updateLogoutText(tcga.uuid.security.getUsername());
        tcga.uuid.security.showLogoutButton();
        tcga.uuid.security.hideLoginButton();
        tcga.uuid.security.updateUIWithAuthoritiesFromServer();
    }
}

/**
 * Update the username value on the Logout button
 *
 * @param text
 */
tcga.uuid.security.updateLogoutText = function(text) {
    Ext.getCmp('logoutButton').setText(text);
}

/**
 * Retrieve the authenticated user's authorities and redraw the UI based on the ROLE-based restrictions
 * applying to the user
 */
tcga.uuid.security.updateUIWithAuthoritiesFromServer = function() {

    var defaultAuthoritiesIfFailure = null;

    Ext.Ajax.request({
        url: SECURITY_AUTHORITIES_JSON_URL,
        method: 'GET',
        success: function (result, request) {
            var reponse = Ext.util.JSON.decode(result.responseText);
            tcga.uuid.security.setAuthenticatedPrincipalAuthorities(reponse.authenticatedPrincipalAuthorities);
            tcga.uuid.security.redrawRestrictedUI();
        },
        failure: function (result, request) {
            tcga.uuid.security.setAuthenticatedPrincipalAuthorities(defaultAuthoritiesIfFailure);
        }
    });
}

/**
 * Retrieve the ACL-based permission of the authenticated user for a given object instance and redraw the UI
 * based on the user's restrictions for that object
 */
tcga.uuid.security.updateUIWithACLAuthoritiesFromServer = function(aclVoterName, objectClazz, objectId, domId) {

    if(tcga.uuid.security.getUsername() != null && aclVoterName != null && objectClazz != null && objectId != null && domId != null) {

        Ext.Ajax.request({
            url: SECURITY_ACL_AUTHORITIES_JSON_URL,
            params: {
                aclVoterName: aclVoterName,
                objectClass: objectClazz,
                objectId: objectId,
                recipient: tcga.uuid.security.getUsername()
            },
            method: 'GET',
            success: function (result, request) {
                var reponse = Ext.util.JSON.decode(result.responseText);
                if(reponse.hasPermission) {
                    tcga.uuid.security.unrestrict(domId);
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

tcga.uuid.security.setAuthenticatedPrincipalAuthorities = function(authenticatedPrincipalAuthoritiesArg) {
    authenticatedPrincipalAuthorities = authenticatedPrincipalAuthoritiesArg;
}

tcga.uuid.security.getAuthenticatedPrincipalAuthorities = function() {
    return authenticatedPrincipalAuthorities;
}

/**
 * Return true if the array contains the item
 *
 * @param array
 * @param item
 */
tcga.uuid.security.contains = function(array, item) {
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
tcga.uuid.security.hasRequiredRole = function(role) {

    var result = false;
    if(tcga.uuid.security.getAuthenticatedPrincipalAuthorities()) {
        result = tcga.uuid.security.contains(tcga.uuid.security.getAuthenticatedPrincipalAuthorities(), role);
    }

    return result;
}

/**
 * Return true if the authenticated principal has all the roles specified in rolArray, false otherwise
 *
 * @param roleArray
 */
tcga.uuid.security.hasRequiredRoles = function(roleArray) {

    if(roleArray) {

        for(var i = 0; i < roleArray.length; i++) {

            if(!tcga.uuid.security.hasRequiredRole(roleArray[i])) {
                return false;
            }
        }
    }

    return true;
}

/**
 * A collection of id of DOM elements that need to be restricted
 */
var restrictedDomIdMixedCollection;
tcga.uuid.security.getRestrictedDomIdMixedCollection = function() {

    if(!restrictedDomIdMixedCollection) {
        restrictedDomIdMixedCollection = new Ext.util.MixedCollection();
    }

    return restrictedDomIdMixedCollection;
}

/**
 * Add an array of ROLE restrictions and an array of ACL restrictions to the DOM element with id domId.
 * In case there are ACL restrictions, the class (clazz) and the class Id (clazzId) of the Java Object Instance
 * to protect need to be specified
 *
 * @param domId
 * @param roleRestrictionArray
 * @param aclRestriction
 * @param clazz
 * @param clazzId
 */
tcga.uuid.security.addRestrictionsMixedCollectionToDomId = function(domId, roleRestrictionArray, aclRestriction, clazz, clazzId) {

    var restrictionsMixedCollection = new Ext.util.MixedCollection();
    restrictionsMixedCollection.add('ROLE', roleRestrictionArray);
    restrictionsMixedCollection.add('ACL', aclRestriction);
    restrictionsMixedCollection.add('CLAZZ', clazz);
    restrictionsMixedCollection.add('CLAZZID', clazzId);

    tcga.uuid.security.getRestrictedDomIdMixedCollection().add(domId, restrictionsMixedCollection);
}

/**
 * For each DOM element registered as a restricted UI element, show it or hide it according to the user's authorities
 */
tcga.uuid.security.redrawRestrictedUI = function(domIdToRedrawArray) {

    if(domIdToRedrawArray) {
        //Redraw only elements provided in domIdToRedrawArray
        for(var i = 0; i < domIdToRedrawArray.length; i++) {
            var domId = domIdToRedrawArray[i];
            tcga.uuid.security.redrawDomId(domId, 'single');
        }

    } else {
        //Redraw all restricted UI elements
        for(var i = 0; i < tcga.uuid.security.getRestrictedDomIdMixedCollection().getCount(); i++) {
            var domId = tcga.uuid.security.getRestrictedDomIdMixedCollection().keys[i];
            tcga.uuid.security.redrawDomId(domId, 'all');
        }
    }
}

/**
 * Redraw the DOM element with Id domId
 * @param domId
 */
tcga.uuid.security.redrawDomId = function(domId, tmp) {

    var restrictionsMixedCollection = tcga.uuid.security.getRestrictedDomIdMixedCollection().get(domId);
    if(restrictionsMixedCollection) {
        var roleArray = restrictionsMixedCollection.get('ROLE');
        var aclVoterName = restrictionsMixedCollection.get('ACL');
        var clazz = restrictionsMixedCollection.get('CLAZZ');
        var clazzId = restrictionsMixedCollection.get('CLAZZID');

        var userHasRequiredRoles = tcga.uuid.security.hasRequiredRoles(roleArray);
        if(userHasRequiredRoles) {

            if(aclVoterName && clazz && clazzId) {
                tcga.uuid.security.updateUIWithACLAuthoritiesFromServer(aclVoterName, clazz, clazzId, domId);
            } else {
                tcga.uuid.security.unrestrict(domId);
            }
        } else {
            tcga.uuid.security.restrict(domId);
        }
    }
}

/**
 * Enable the DOM element with Id domId
 * @param domId
 */
tcga.uuid.security.restrict = function(domId) {

    if(Ext.getCmp(domId)) {
        Ext.getCmp(domId).setDisabled(true);
        Ext.getCmp(domId).setVisible(false);
    }
}

/**
 * Enable the DOM element with Id domId
 * @param domId
 */
tcga.uuid.security.unrestrict = function(domId) {
    
    if(Ext.getCmp(domId)) {
        Ext.getCmp(domId).setDisabled(false);
    }
}
