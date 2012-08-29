Hudson Plugin Central
=====================

Hudson Plugin Central is a Admin UI for Plugin update site. 

Building
========

mvm clean install

plugin-central.war is created under webapp/target

Running
=======

java -jar plugin-central.war --httpPort=9696 --update-site=update-site.json


Metadata information provided in the update-site.json are

{  
    "update-site-local-path" : "/opt/hudson/update-site3",
    "update-center-json-local-path" : "/opt/hudson/update-site3/update-center.json",
    "plugins-download-local-path-root" : "/opt/hudson/update-site3/downloads/plugins",
    "plugins-temp-download-local-path-root" : "/opt/hudson/update-site3/tmp-plugins",
    "shiro-security-ini-local-path" : "/opt/hudson/update-site3/shiro-security.ini",
    "plugins-download-root-url" : "http://hudson-ci.org/update-center3/downloads/plugins"
}

shiro-security.ini may have the following

[users]
# user 'sysadmin' with sysadmin privilege
sysadmin = <sysadmin-password>, sysadmin

# user 'admin' with admin privilege
admin = <admin-password>, admin

# user 'hudson' as 'developer' role
hudson = <developer-password>, developer


[roles]
# 'sysadmin' role has all permissions, indicated by the wildcard '*'
sysadmin = *
# 'admin' role has most other permissions (update, delete, upload) plugins
admin = plugin:*

# The 'developer' role can update plugins
developer = plugin:update








 