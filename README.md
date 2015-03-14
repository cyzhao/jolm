# JOLM - Java Object LDAP Mapping

This is a little library built on top of Spring LDAP to provide more convenient way of accessing LDAP using Spring framework. 

Consists of two components:

* A maven plugin that generates Java LDAP entities (JavaBean's) from LDAP DXC configuration files.
* An extension to Spring's SimpleLdapTemplate that provides CRUD operations for any generated LDAP entity.
