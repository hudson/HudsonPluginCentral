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

Accessing
=========

http://localhost:9696/PluginCentral3

Setup
=====


Metadata information provided in the update-site.json are

<pre><code>


{  
    "update-site-root" : "/opt/hudson/update-site3",
    "update-site-local-path" : "",
    "update-center-json-local-path" : "update-center.json",
    "plugins-download-local-path-root" : "downloads/plugins",
    "plugins-temp-download-local-path-root" : "tmp-plugins",
    "shiro-security-ini-local-path" : "shiro-security.ini",
    "l10n-store-path" : "l10n",
    "plugins-download-root-url" : "http://hudson-ci.org/update-center3/downloads/plugins",
    "usage-stats-store-path" : "stats",
    "usage-stats-private-key" : "{Your private key}"
}

</code><pre>


shiro-security.ini may have the following

<pre><code>

[users]
# user 'sysadmin' with sysadmin privilege
sysadmin = sysadmin-password, sysadmin

# user 'admin' with admin privilege
admin = admin-password, admin

# user 'hudson' as 'developer' role
hudson = developer-password, developer


[roles]
# 'sysadmin' role has all permissions, indicated by the wildcard '*'
sysadmin = *
# 'admin' role has most other permissions (update, delete, upload) plugins
admin = plugin:*

# The 'developer' role can update plugins
developer = plugin:update

</code></pre>








 