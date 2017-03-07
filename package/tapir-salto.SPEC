Name               : %{_app_name}
Version            : %{_version}
Release            : el6
Summary            : %{_app_summary}
License            : Restricted
URL                : http://sip3.io
Vendor             : Sip3.io
Packager           : windsent@sip3.io
BuildArch          : noarch
Provides           : java
Requires           : jre >= 1.8, initscripts >= 9.0, chkconfig >= 1.3

%define __os_install_post %{nil}
%description
%{_app_description}

%prep
rm -rf $RPM_BUILD_ROOT
rm -rf $RPM_SOURCE_DIR

%install
    mkdir -p $RPM_BUILD_ROOT/etc/%{_app_name}
    mkdir -p $RPM_BUILD_ROOT/opt/%{_app_name}
    mkdir -p $RPM_BUILD_ROOT/etc/%{_app_name}/template
    mkdir -p $RPM_BUILD_ROOT/var/log/%{_app_name}
    umask 0222

    # application files
    install -D %{_srcdir}/%{_app_dir}/target/%{_app_name}.jar $RPM_BUILD_ROOT/opt/%{_app_name}/%{_app_name}.jar
    install -D %{_srcdir}/package/opt/%{_app_name}/instance $RPM_BUILD_ROOT/opt/%{_app_name}/instance

    # configuration files
    install -D %{_srcdir}/package/etc/%{_app_name}/logback.xml.example $RPM_BUILD_ROOT/etc/%{_app_name}/logback.xml.example
    install -D %{_srcdir}/package/etc/%{_app_name}/%{_app_name}.properties.example $RPM_BUILD_ROOT/etc/%{_app_name}/%{_app_name}.properties.example
    install -D %{_srcdir}/package/etc/%{_app_name}/%{_app_name}.properties.changes $RPM_BUILD_ROOT/etc/%{_app_name}/%{_app_name}.properties.changes
    install -D %{_srcdir}/package/etc/%{_app_name}/%{_app_name}.default $RPM_BUILD_ROOT/etc/%{_app_name}/%{_app_name}.default
    install -D %{_srcdir}/package/etc/%{_app_name}/hosts.yml.example $RPM_BUILD_ROOT/etc/%{_app_name}/hosts.yml.example
    install -D %{_srcdir}/package/etc/init.d/%{_app_name} $RPM_BUILD_ROOT/etc/init.d/%{_app_name}
   
    # template files
    install -D %{_srcdir}/package/etc/%{_app_name}/template/logback.xml $RPM_BUILD_ROOT/etc/%{_app_name}/template/logback.xml
    install -D %{_srcdir}/package/etc/%{_app_name}/template/%{_app_name}.properties $RPM_BUILD_ROOT/etc/%{_app_name}/template/%{_app_name}.properties

    
%files

%defattr(0644, tapir, tapir)
%config /etc/%{_app_name}/%{_app_name}.properties.example
%config /etc/%{_app_name}/%{_app_name}.properties.changes
%config /etc/%{_app_name}/logback.xml.example
%config /etc/%{_app_name}/template/logback.xml
%config /etc/%{_app_name}/template/%{_app_name}.properties
%config /etc/%{_app_name}/%{_app_name}.default
%config /etc/%{_app_name}/hosts.yml.example
/opt/%{_app_name}/%{_app_name}.jar

%defattr(0755, tapir, tapir)
/etc/init.d/%{_app_name}
/opt/%{_app_name}/instance
%dir /etc/%{_app_name}
%dir /etc/%{_app_name}/template
%dir /var/log/%{_app_name}

%clean
rm -rf $RPM_BUILD_ROOT
rm -rf $RPM_SOURCE_DIR

%pre
getent group tapir &> /dev/null || \
       groupadd -r tapir &> /dev/null
getent passwd tapir &> /dev/null || \
       useradd -r -g tapir -d /opt/tapir -s /sbin/nologin tapir &> /dev/null

%post
# Initial installation (always, for new service)
/sbin/chkconfig --add %{_app_name}
/sbin/chkconfig %{_app_name} on
if [ $1 -eq 1 ]; then
   if [ ! -f /etc/%{_app_name}/%{_app_name}.properties ]; then 
      cp /etc/%{_app_name}/%{_app_name}.properties.example /etc/%{_app_name}/%{_app_name}.properties
   fi
   if [ ! -f /etc/%{_app_name}/logback.xml ]; then
      cp /etc/%{_app_name}/logback.xml.example /etc/%{_app_name}/logback.xml
   fi
   if [ ! -f /etc/%{_app_name}/hosts.yml.example ]; then
      cp /etc/%{_app_name}/hosts.yml.example /etc/%{_app_name}/hosts.yml
   fi
fi

%preun
if [ $1 -eq 0 ]; then
  # Package removal, not upgrade
  /sbin/service %{_app_name} stop &> /dev/null
  /sbin/chkconfig --del %{_app_name} &> /dev/null
  for i in `ls /etc/init.d/%{_app_name}-* 2>/dev/null`; do 
     echo "%{_app_name} instance presents: $i"
     $i stop &> /dev/null
  done
fi

%postun
if [ $1 -eq 0 ]; then
    # Perform tasks to uninstall
    rm -rf /etc/%{_app_name}
    rm -rf /var/log/%{_app_name}
    rm -rf /opt/%{_app_name}
    rm -f /etc/init.d/%{_app_name}*
fi

