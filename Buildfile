# -*- coding: utf-8 -*-
# -*- mode: ruby -*-
# Large amounts of memory ensure a fast build
ENV['JAVA_OPTS'] ||= '-Xms512m -Xmx768m'


# Additional maven repositories 
repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << 'http://people.apache.org/repo/m2-incubating-repository'
repositories.remote << 'http://guiceyfruit.googlecode.com/svn/repo/releases'
repositories.remote << 'http://download.java.net/maven/2'
repositories.remote << 'http://static.appfuse.org/repository'
repositories.remote << 'http://repository.jboss.org/maven2'
repositories.remote << 'http://google-maven-repository.googlecode.com/svn/repository'


# Don't touch below unless you know what you are doing
# --------------------------------------------------------------------------------------------------
#
require "open3"

VERSION_NUMBER = '0.3.5'
VERSION_NAME = 'Richie'
FALLBACK_VERSION_TAG = 'release-Richie'
GROUP_NAME = 'de.zib.gndms'
MF_COPYRIGHT = 'Copyright 2008-2011 Zuse Institute Berlin (ZIB)'
LICENSE ='This software has been licensed to you under the terms and conditions of the Apache License 2.0 (APL 2.0) only.'
MF_LICENSE="#{LICENSE}  See META-INF/LICENSE for detailed terms and conditions."
USERNAME = ENV['USER'].to_s
Open3.popen3( "git describe --tags" ) do |stdin,stdout,stderr|
    commit_id = stdout.gets
    if ( commit_id.nil? )
        VERSION_TAG = FALLBACK_VERSION_TAG 
    else 
        VERSION_TAG = commit_id.chomp
    end
end

# Yes, this project uses java
require 'buildr/java'
include Java
include Commands

# Helper to create non-standard GNDMS sub-project layouts
require 'buildr/gndms'
include GNDMS


# Test environment
testEnv('GLOBUS_LOCATION', 'the root directory of Globus Toolkit 4.0.8')
GNDMS_DB=[ ENV['GLOBUS_LOCATION'], 'etc', 'gndms_shared', 'db', 'gndms' ].join(File::SEPARATOR)
DEPLOY_GAR=[ ENV['GLOBUS_LOCATION'], 'bin', 'globus-deploy-gar' ].join(File::SEPARATOR)
testEnv('ANT_HOME', 'the root directory of Apache Ant')
testEnv('JAVA_HOME', 'the root directory of J2SE')
JAVA_HOME = ENV['JAVA_HOME']
# ENV['PATH'] = File.join([ENV['JAVA_HOME'], 'bin']) + File::PATH_SEPARATOR + ENV['PATH']
SOURCE = '1.5'
TARGET = '1.5'
testEnv('GNDMS_SOURCE', 'the root directory of GNDMS source distribution (i.e. the toplevel directory in which the Buildfile resides)')
testEnv('GNDMS_SHARED', '$GLOBUS_LOCATION/etc/gndms_shared')
testEnv('GNDMS_MONI_CONFIG', '$GNDMS_SHARED/monitor.properties')
testEnv('USER', 'your user\'s login (your UNIX is weird)')
testTool('rsync')
testTool('curl')
testTool('openssl')
testTool('hostname')
HOSTNAME = `hostname`.split[0]

puts "GNDMS #{VERSION_NUMBER} \”#{VERSION_NAME}\" #{VERSION_TAG}"
puts MF_COPYRIGHT
puts "#{LICENSE}  Please consult doc/licensing about licensing conditions of downloaded 3rd party software."
if ENV['GNDMS_DEPS']=='skip' then 
	puts 'This run will not provide GT4 with dependencies.'
else
	if ENV['GNDMS_DEPS']=='link' then 
		puts 'This run will use symlinks to provide GT4 with dependencies.' 
	else
		puts 'This run will use file copying to provide GT4 with dependencies.'
	end
end

# ant binary to use
gndms_ant = ENV['GNDMS_SOURCE'] + '/bin/gndms-ant'

# Helper to construct GT4 jar pathes
require 'buildr/gt4'
include GT4

# Essentially GT4 package management is classloading unaware crap
# Therefore we have to filter out some jars in order to avoid invalid jar-shadowing through dependencies
def skipDeps(deps) 
  deps = deps.select { |ding| !ding.include?("/commons-cli") }
  deps = deps.select { |ding| !ding.include?("/commons-logging") }
  deps = deps.select { |ding| !ding.include?("/commons-lang-2.1") }
  deps = deps.select { |ding| !ding.include?("/commons-pool") }
  return deps
end

# Non-GT4 dependencies
#GUICE = 'com.google.code.guice:guice:jar:2.0'
GUICE = 'com.google.inject:guice:jar:2.0'
GOOGLE_COLLECTIONS = 'com.google.code.google-collections:google-collect:jar:snapshot-20080530'
JETBRAINS_ANNOTATIONS = 'com.intellij:annotations:jar:7.0.3'
JODA_TIME = transitive('joda-time:joda-time:jar:1.6')
CXF = 'org.apache.cxf:cxf-bundle:jar:2.1.4'
JAXB = 'javax.xml.bind:jaxb-api:jar:2.2.1'
STAX = 'stax:stax-api:jar:1.0.1'
COMMONS_COLLECTIONS = transitive(['commons-collections:commons-collections:jar:3.2'])
COMMONS_CODEC = 'commons-codec:commons-codec:jar:1.4'
COMMONS_LANG = 'commons-lang:commons-lang:jar:2.1'
COMMONS_FILEUPLOAD = transitive(['commons-fileupload:commons-fileupload:jar:1.2.1'])
JETTY = ['org.mortbay.jetty:jetty:jar:6.1.11', 'org.mortbay.jetty:jetty-util:jar:6.1.11']
GROOVY = ['org.codehaus.groovy:groovy:jar:1.6.9']
ARGS4J = 'args4j:args4j:jar:2.0.14'
SLF4J = transitive( ['org.slf4j:slf4j-log4j12:jar:1.5.8'])
# TESTNG = download(artifact('org.testng:testng:jar:5.1-jdk15') => 'http://static.appfuse.org/repository/org/testng/testng/5.1/testng-5.1-jdk15.jar')
DB_DERBY = ['org.apache.derby:derby:jar:10.5.3.0', 'org.apache.derby:derbytools:jar:10.5.3.0']

HTTP_CORE = ['org.apache.httpcomponents:httpcore:jar:4.0', 'org.apache.httpcomponents:httpcore-nio:jar:4.0', 'org.apache.httpcomponents:httpclient:jar:4.0.1']
JNA = [ 'com.sun.jna:jna:jar:3.0.9' ]

# Grouped GT4 dependencies
GT4_COMMONS = gt4jars(['commons-beanutils.jar', 
                       'commons-digester.jar',
                       'commons-discovery.jar',
                       'commons-pool.jar'])
GT4_LOG = gt4jars(['commons-logging.jar', 'log4j-1.2.15.jar'])
GT4_COG = gt4jars(['cog-axis.jar', 'cog-jglobus.jar', 'cog-url.jar'])
GT4_AXIS = gt4jars(['axis.jar', 'axis-url.jar', 'saaj.jar'])
GT4_WSRF = gt4jars(['addressing-1.0.jar',
                    'axis-url.jar',
                    'axis.jar',
                    'commonj.jar',
                    'concurrent.jar',
                    # 'globus_wsrf_rft_stubs.jar',
                    'naming-common.jar',
                    'wsdl4j.jar',
                    'saaj.jar',
                    'wsrf_common.jar',
                    'wsrf_core.jar',
                    'wsrf_core_stubs.jar',
                    'wsrf_tools.jar'])
GT4_SERVLET = gt4jars(['servlet.jar'])
GT4_SEC = gt4jars(['puretls.jar', 'opensaml.jar', 
                   'cryptix-asn1.jar', 'cryptix.jar', 'cryptix32.jar', 
                   'jce-jdk13-125.jar', 'wss4j.jar', 'jgss.jar', 
                   'globus_delegation_service.jar',
                   'globus_delegation_stubs.jar'])
GT4_XML = gt4jars(['xalan-2.6.jar', 'xercesImpl-2.7.1.jar', 'xml-apis.jar', 'xmlsec.jar', 'jaxrpc.jar'])
GT4_GRAM = gt4jars(['gram-monitoring.jar', 'gram-service.jar', 'gram-stubs.jar', 'gram-utils.jar'])
GT4_USEAGE = gt4jars([ 'globus-usage-core.java' ])
GT4_MDS = gt4jars(['globus_wsrf_mds_aggregator.jar',
                   'globus_wsrf_mds_aggregator_stubs.jar',
                   'webmds-0.1-dev.jar',
                   'wsrf_mds_index.jar',
                   'wsrf_mds_index_stubs.jar',
                   'wsrf_mds_trigger.jar',
                   'wsrf_mds_trigger_stubs.jar',
                   'wsrf_mds_usefulrp.jar',
                   'wsrf_mds_usefulrp_schema_stubs.jar'])


# OpenJPA is required by gndms:model
OPENJPA = [ COMMONS_LANG, 'org.apache.openjpa:openjpa-all:jar:2.0.0']

require 'buildr/openjpa2'
include Buildr::OpenJPA2

# helper for derby ij
require 'buildr/derbyij'
include DERBYIJ


desc 'Germanys Next Data Management System'
define 'gndms' do
    project.version = VERSION_NUMBER
    project.group = GROUP_NAME
    manifest['Copyright'] = MF_COPYRIGHT
    manifest['License'] = MF_LICENSE
    compile.options.source = SOURCE
    compile.options.target = TARGET
    # compile.options.lint = 'all'
    meta_inf << file(_('LICENSE'))
    meta_inf << file(_('GNDMS-RELEASE'))
    test.using :testng
    @buildInfo = nil
    @releaseInfo = nil

    # WSRF GT4 services to be built
    SERVICES = ['GORFX', 'DSpace']
    # TODO: Replace with some ruby magic
    DSPACE_STUBS   = file(_('services/DSpace/build/lib/gndms-dspace-stubs.jar').to_s)
    DSPACE_CLIENT  = _('services/DSpace/build/lib/gndms-dspace-client.jar')
    DSPACE_COMMON  = _('services/DSpace/build/lib/gndms-dspace-common.jar')
    DSPACE_SERVICE = _('services/DSpace/build/lib/gndms-dspace-service.jar')
    DSPACE_TESTS   = _('services/DSpace/build/lib/gndms-dspace-tests.jar')
    GORFX_STUBS   = file(_('services/GORFX/build/lib/gndms-gorfx-stubs.jar').to_s)
    GORFX_CLIENT  = _('services/GORFX/build/lib/gndms-gorfx-client.jar')
    GORFX_COMMON  = _('services/GORFX/build/lib/gndms-gorfx-common.jar')
    GORFX_SERVICE = _('services/GORFX/build/lib/gndms-gorfx-service.jar')
    GORFX_TESTS   = _('services/GORFX/build/lib/gndms-gorfx-tests.jar')
    SERVICE_STUBS = [GORFX_STUBS, DSPACE_STUBS]
    EXTRA_JARS = [ _('extra/caGrid-Introduce-serviceTools-1.2.jar'),
                   _('extra/caGrid-ServiceSecurityProvider-client-1.2.jar'),
                   _('extra/caGrid-ServiceSecurityProvider-common-1.2.jar'),
                   _('extra/caGrid-ServiceSecurityProvider-service-1.2.jar'),
                   _('extra/caGrid-ServiceSecurityProvider-stubs-1.2.jar'),
                   _('extra/caGrid-advertisement-1.2.jar'),
                   _('extra/caGrid-core-1.2.jar'),
                   _('extra/caGrid-metadata-security-1.2.jar'),
                   _('extra/castor-0.9.9.jar'),
                   _('extra/jdom-1.0.jar')]


    def updateBuildInfo()
      if (@buildInfo == nil) then
        buildFile = File.new(_('GNDMS-BUILD-INFO'), 'w')
        timestamp = Time.now.to_s
        @buildInfo = 'built-at: ' + timestamp + ' built-by: ' + USERNAME + '@' + HOSTNAME
        buildFile.syswrite(@buildInfo)
        buildFile.close
        puts '>>>> '
        puts '>>>> GNDMS_BUILD_INFO is \'' + @buildInfo + '\''
        puts '>>>> '
      end
    end

    task 'update-build-info' do updateBuildInfo() end

    def updateReleaseInfo()
      if (@releaseInfo == nil) then
        @releaseInfo = "Generation N Data Management System VERSION: #{VERSION_NUMBER} \"#{VERSION_NAME}\" #{VERSION_TAG}"
        relFile = File.new(_('GNDMS-RELEASE'), 'w')
        relFile.syswrite(@releaseInfo)
        relFile.close
        puts '>>>> '
        puts '>>>> GNDMS_RELEASE is \'' + @releaseInfo + '\''
        puts '>>>> '
      end
    end

    task 'update-release-info' do updateReleaseInfo() end

    meta_inf << file(_('GNDMS-BUILD-INFO'))

    desc 'GT4-independent utility classes for GNDMS'
    define 'stuff', :layout => dmsLayout('stuff', 'gndms-stuff') do
       task( 'update-release-info' )
       compile.with GUICE, GOOGLE_COLLECTIONS, JETBRAINS_ANNOTATIONS, SLF4J
       compile { project('gndms').updateBuildInfo() }
       package :jar
    end

    desc 'Shared database model classes'
    define 'model', :layout => dmsLayout('model', 'gndms-model') do
      # TODO: Better XML
      compile.with project('stuff'), COMMONS_COLLECTIONS, COMMONS_LANG, GOOGLE_COLLECTIONS, JODA_TIME, JETBRAINS_ANNOTATIONS, GUICE, CXF, OPENJPA, JAXB, STAX
      compile { open_jpa_enhance }
      package :jar
    end

    desc 'GT4-dependent utility classes for GNDMS'
    define 'kit', :layout => dmsLayout('kit', 'gndms-kit') do
      compile.with JETTY, GROOVY, GOOGLE_COLLECTIONS, COMMONS_FILEUPLOAD, COMMONS_CODEC, project('stuff'), project('model'), JETBRAINS_ANNOTATIONS, GT4_LOG, GT4_COG, GT4_AXIS, GT4_SEC, GT4_XML, JODA_TIME, ARGS4J, GUICE, GT4_SERVLET, COMMONS_LANG, OPENJPA, SLF4J, JNA
      compile
      package :jar
    end

    desc 'GNDMS logic classes (actions for manipulating resources)'
    define 'logic', :layout => dmsLayout('logic', 'gndms-logic') do
       compile.with JETBRAINS_ANNOTATIONS, project('kit'), project('stuff'), project('model'), JODA_TIME, GOOGLE_COLLECTIONS, GUICE, DB_DERBY, GT4_LOG, GT4_AXIS, GT4_COG, GT4_SEC, GT4_XML, COMMONS_LANG, OPENJPA
       compile
       package :jar
    end

    desc 'GNDMS classes for dealing with wsrf and xsd types'
    define 'gritserv', :layout => dmsLayout('gritserv', 'gndms-gritserv') do
      compile.with JETBRAINS_ANNOTATIONS, project('kit'), project('stuff'), project('model'), ARGS4J, JODA_TIME, GORFX_STUBS, OPENJPA, GT4_LOG, GT4_WSRF, GT4_COG, GT4_SEC, GT4_XML, GT4_COMMONS, COMMONS_LANG, COMMONS_COLLECTIONS, COMMONS_CODEC, GUICE
      compile
      package :jar
    end

    desc 'GNDMS core infrastructure classes'
    define 'infra', :layout => dmsLayout('infra', 'gndms-infra') do
      # Infra *must* have all dependencies since we use this list in copy/link-deps
      compile.with JETBRAINS_ANNOTATIONS, OPENJPA, project('gritserv'), project('logic'), project('kit'), project('stuff'), project('model'), ARGS4J, JODA_TIME, JAXB, GT4_SERVLET, JETTY, CXF, GROOVY, GOOGLE_COLLECTIONS, GUICE, DB_DERBY, GT4_LOG, GT4_WSRF, GT4_GRAM, GT4_COG, GT4_SEC, GT4_XML, JAXB, GT4_COMMONS, COMMONS_CODEC, COMMONS_LANG, COMMONS_COLLECTIONS, HTTP_CORE, TestNG.dependencies, COMMONS_FILEUPLOAD, JNA
      compile
      package :jar
      doc projects('gndms:stuff', 'gndms:model', 'gndms:gritserv', 'gndms:kit', 'gndms:logic')

      # Symlink or copy all dependencies of infra + the infra jar - whatever gets filtered by skipDeps to GT4LIB
      # and log source jars used to lib/DEPENDENCIES and lib/dependencies.xml (both files are not further used by the build - FYI only)
      def installDeps(copy)
        deps = Buildr.artifacts(project('infra').compile.dependencies).map(&:to_s)
        deps << project('infra').package.to_s
		    EXTRA_JARS.each do |jar|
					deps << jar
				end
        deps = skipDeps(deps)

        classpathFile = File.new(GT4LIB + '/gndms-dependencies.xml', 'w')
        classpathFile.syswrite('<?xml version="1.0"?>' + "\n" + '<project><target id="setGNDMSDeps"><path id="service.build.extended.classpath">' + "\n")
        depsFile = File.new(GT4LIB + '/gndms-dependencies', 'w')
        deps.select { |jar| jar[0, GT4LIB.length] != GT4LIB }.each { |file| 
           basename = File.basename( file )
           newname = GT4LIB+'/'+basename
           if (copy)
             puts 'cp: \'' + file + '\' to: \'' + newname + '\''
             cp(file, newname)
             puts 'yay'
             chmod 0644, newname
           else
             puts 'ln_sf: \'' + file + '\' to: \'' + newname + '\''
             chmod 0644, file
             ln_sf(file, newname)
           end
           depsFile.syswrite(basename + "\n") 
           classpathFile.syswrite('<pathelement location="' + basename + '" />' + "\n")
        }
        depsFile.close
        classpathFile.syswrite('</path></target></project>' + "\n\n")
        classpathFile.close
      end

      desc 'Install dependencies to $GLOBUS_LOCATION/lib (execute as globus user)'
      task 'install-deps' => 'artifacts' do
	if (ENV['GNDMS_DEPS'] != 'skip') then
        	installDeps(ENV['GNDMS_DEPS']!='link')
	end
        # Fix monitor.properties permissions
        system "test -d '#{ENV['GNDMS_SHARED']}' || mkdir '#{ENV['GNDMS_SHARED']}'"
        propsFile = [ ENV['GNDMS_SHARED'], 'monitor.properties' ].join(File::SEPARATOR)
        system "touch '#{propsFile}'"
        system "chmod 0600 '#{propsFile}'"
      end

    end


    desc 'rsync type schemata between services and types'
    task :typesync  => :package do
      SERVICES.each { |service|
        system 'rsync -aurl ' + _('../services' + service + '/build/schema/') + ' ' + _('types/')
        system 'rsync -aurl ' + _('../services' + service + '/schema/') + ' ' + _('types/')
      }
    end

    task 'clean-services' do
      SERVICES.each { |service| 
        system 'cd ' + _('services/'+service) + ' && ' + gndms_ant + ' clean'
      }
    end

    task 'package-stubs' do
      SERVICES.each { |service| 
        system "cd '#{_('services', service)}' && " + gndms_ant + " jarStubs"
      }
    end

    task 'link-services' do
      SERVICES.each { |service| 
        lcService = service.downcase
        [ "gndms-#{lcService}-stubs.jar", "gndms-#{lcService}-common.jar", "gndms-#{lcService}-client.jar", "gndms-#{lcService}-tests.jar" ].each { |jar|
          ln_sf(_('services', service, 'build', 'lib', jar), GT4LIB)
        }        
      }
    end

    desc 'Create DSpace GAR for deployment (Requires packaged GNDMS and installed dependencies)'
    task 'package-DSpace' do
      system "cd '#{_('services', 'DSpace')}' && " + gndms_ant + " createDeploymentGar"
      # ln_sf(_('services', 'DSpace', 'gndms_DSpace.gar'), _('.'))
    end

    desc 'Deploy current gndms_DSpace.gar'
    task 'deploy-DSpace' do
       system "#{DEPLOY_GAR} '#{_('services', 'DSpace', 'gndms_DSpace.gar')}'"
    end

    task 'rebuild-DSpace' => [task('package-DSpace'), task('deploy-DSpace')] do
      system "cd '#{_('services', 'DSpace')}' && " + gndms_ant + " jars"
    end

    # file DSPACE_STUBS.to_s => task('gndms:package-stubs') do end
    # file DSPACE_COMMON.to_s => task('gndms:rebuild-DSpace') do end
    # file DSPACE_CLIENT.to_s => task('gndms:rebuild-DSpace') do end
    # file DSPACE_SERVICE.to_s => task('gndms:rebuild-DSpace') do end
    # file DSPACE_TESTS.to_s => task('gndms:rebuild-DSpace') do end

    desc 'Create GORFX GAR for deployment (Requires packaged GNDMS and installed dependencies)'
    task 'package-GORFX' do
      system "cd '#{_('services', 'GORFX')}' && " + gndms_ant + " createDeploymentGar"
      # ln_sf(_('services', 'GORFX', 'gndms_GORFX.gar'), _('.'))
    end

    desc 'Deploy current gndms_GORFX.gar'
    task 'deploy-GORFX' do
      system "#{DEPLOY_GAR} '#{_('services', 'GORFX', 'gndms_GORFX.gar')}'"
    end

    task 'rebuild-GORFX' do
      system "cd '#{_('services', 'GORFX')}' && " + gndms_ant + " jars"
    end

    # file GORFX_STUBS.to_s => task('gndms:package-stubs') do end
    # file GORFX_COMMON.to_s => task('gndms:rebuild-GORFX') do end
    # file GORFX_CLIENT.to_s => task('gndms:rebuild-GORFX') do end
    # file GORFX_SERVICE.to_s => task('gndms:rebuild-GORFX') do end
    # file GORFX_TESTS.to_s => task('gndms:rebuild-GORFX') do end

    desc 'Peek into the gndms derby database'
    task 'derby-ij' do
        callIJ( GNDMS_DB, DB_DERBY )
    end

    define 'gndmc', :layout => dmsTestLayout('gndmc', 'gndms-gndmc') do
      compile.with JETBRAINS_ANNOTATIONS, OPENJPA, project('gndms:gritserv'), project('gndms:kit'), project('gndms:stuff'), project('gndms:model'), ARGS4J, SERVICE_STUBS, GORFX_CLIENT, DSPACE_CLIENT, GORFX_COMMON, DSPACE_COMMON, COMMONS_CODEC, COMMONS_COLLECTIONS, GT4_COMMONS, JODA_TIME, GT4_GRAM, GT4_LOG, GT4_WSRF, GT4_COG, GT4_SEC, GT4_XML, EXTRA_JARS, GT4_MDS, TestNG.dependencies
      compile
      test.compile
      test.exclude '*'
      package :jar

      task 'show-log' do
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        uri  = ENV['URI']
        if (uri == nil) then
          puts 'Call with env URI="GORFX or DSpace EPR" ARGS="Arguments to .sys.ReadContainerLog'
          exit 1
        end
        args = ENV['ARGS']
        if (args == nil) then args = "" end
        full_args = [ '-uri', uri, '.sys.ReadContainerLog' ]
        full_args << args  
        Commands.java('de.zib.gndmc.MaintenanceClient',  full_args, { :classpath => jars, :verbose => true } )
      end

      task 'show-version' do
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        uri  = ENV['URI']
        if (uri == nil) then
          puts 'Call with env URI="GORFX or DSpace EPR" ARGS="Arguments to .sys.ReadGNDMSVersion'
          exit 1
        end
        args = ENV['ARGS']
        if (args == nil) then args = "" end
        full_args = [ '-uri', uri, '.sys.ReadGNDMSVersion' ]
        full_args << args  
        Commands.java('de.zib.gndmc.MaintenanceClient',  full_args, { :classpath => jars, :verbose => true } )
      end

      task 'run-test' do
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        args = [ '-p', ENV['GNDMS_SOURCE']+'/etc/sliceInOutClient.properties' ]
        Commands.java('de.zib.gndmc.SliceInOutClient',  args, 
                      { :classpath => jars, :properties => 
                          { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" } } )
      end

      task 'run-staging-test' do
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        #host = `hostname`.chomp
        host = 'csr-pc25.zib.de'
        dn = `grid-proxy-info -identity`
        dn = dn.chomp
        if (ENV['GNDMS_SFR'] == nil)
            prop = 'etc/sfr/dummy-sfr.properties'
        else 
            prop = ENV['GNDMS_SFR']
        end
        args = [ '-props', prop, 
                 '-uri', 'https://' + host + ':8443/wsrf/services/gndms/GORFX',
	             '-dn', dn
        ]
        Commands.java('de.zib.gndmc.GORFX.c3grid.ProviderStageInClient',  args, 
                      { :classpath => jars, :properties => 
                          { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" } } )
      end

      task 'run-stress-test' do
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        if (ENV['GORFX_URI'] == nil)
            host = `hostname`.chomp
        else 
            host = ENV['GORFX_URI']
            puts "using", host
        end
        dn = `grid-proxy-info -identity`
        dn = dn.chomp
        if (ENV['GNDMS_PROPS'] == nil)
            prop = 'test-data/test-properties/multi_file_transfer_awi.properties'
        else 
            prop = ENV['GNDMS_PROPS']
        end
        args = [ '-props', prop, 
                 '-uri', 'https://' + host + ':8443/wsrf/services/gndms/GORFX',
	             '-dn', dn
        ]
        Commands.java('de.zib.gndmc.GORFX.diag.MultiRequestClient',  args, 
                      { :classpath => jars, :properties => 
                          { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" } } )
      end

      desc 'runs the interSliceTransfer test'
      task 'run-ist' do 

        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        if (ENV['gorfx_host'] == nil)
            gorfx_host = `hostname`.chomp
        else 
            gorfx_host = ENV['gorfx_host']
        end
        if (ENV['dspace_host'] == nil)
            dspace_host = `hostname`.chomp
        else 
            dspace_host = ENV['dspace_host']
        end
        dn = `grid-proxy-info -identity`
        dn = dn.chomp
        if (ENV['GNDMS_PROPS'] == nil)
            prop = 'test-data/test-properties/multi_file_transfer_awi.properties'
        else 
            prop = ENV['GNDMS_PROPS']
        end
        args = [ '-props', prop, 
                 '-uri', 'https://' + gorfx_host + ':8443/wsrf/services/gndms/GORFX',
                 '-duri', 'https://' + dspace_host + ':8443/wsrf/services/gndms/DSpace',
	             '-dn', dn
        ]
        puts args
        Commands.java('de.zib.gndmc.GORFX.InterSliceTransferClient',  args, 
                      { :classpath => jars, :properties => 
                          { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" } } )
      end

      desc 'runs the interSliceTransfer test'
      task 'run-rft' do 

        runner = 'de.zib.gndmc.GORFX.diag.RemoteFileTransferTest'
        jars = compile.dependencies.map(&:to_s)
        jars << compile.target.to_s
        if (ENV['GNDMS_PROPS'] == nil)
            prop = 'test-data/simple-rft.properties'
        else 
            prop = ENV['GNDMS_PROPS']
        end
        args = [ '-props', prop, 
                 '-uri', 'https://csr-pc25.zib.de:8443/wsrf/services/gndms/GORFX', #'https://' + gorfx_host + ':8443/wsrf/services/gndms/GORFX',
                 '-uid', `id -u`.chomp,
                 '-cancel', 30000
        ]
        props = { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" }



        puts args
        unless ENV["GEN_SCRIPT"] == "1"
            Commands.java( runner,  args, 
                          { :classpath => jars, :properties => 
                              props } )
        else
            genScript( 'run-rft', runner, args, jars, props )
        end
      end

end
end


# Utility stuff

task 'show-log' => task('gndms:gndmc:show-log')


# Database stuff

task 'kill-db' do
    rm_rf GNDMS_DB
    puts 'ATTENTION Do not forget to call fix-permissions after you have recreated the database'
end

task 'inspect-db' => task('gndms:derby-ij') 

# Nice toplevel targets that redirect to service targets

task 'package-DSpace' => task('gndms:package-DSpace')
task 'deploy-DSpace' => task('gndms:deploy-DSpace')
task 'rebuild-DSpace' => task('gndms:rebuild-DSpace')

task 'package-GORFX' => task('gndms:package-GORFX') 
task 'deploy-GORFX' => task('gndms:deploy-GORFX') 
task 'rebuild-GORFX' => task('gndms:rebuild-GORFX') 

task 'clean-services' => task('gndms:clean-services')
task 'package-stubs' => task('gndms:package-stubs')

task 'rebuild-services' => [task('rebuild-DSpace'), task('rebuild-GORFX')] 
task 'package-services' => [task('package-DSpace'), task('package-GORFX')] 
task 'deploy-services' => [task('deploy-DSpace'), task('deploy-GORFX')] 
task 'link-services' => task('gndms:link-services') 


# Doc targets

task('clean-apidocs') do
    rm_rf [ENV['GNDMS_SOURCE'], 'doc', 'api'].join(File::SEPARATOR)
end

task('compile-apidocs') do
    task('gndms:infra:doc').invoke
end

task :apidocs => task('compile-apidocs')

# Per-Grid Targets

task 'ptgrid-setupdb' do
    system "#{ENV['GNDMS_SOURCE']}/scripts/ptgrid/setup-resource.sh CREATE"
end

task 'ptgrid-test' do
    system "#{ENV['GNDMS_SOURCE']}/scripts/ptgrid/test-resource.sh"
end

desc 'Sets up the c3 data-provider database'
task 'c3grid-dp-setupdb' do
    system "#{ENV['GNDMS_SOURCE']}/scripts/c3grid/setup-dataprovider.sh CREATE"
end

task 'install-chown-script' do
    system "install -o 0 -g 0 -m 700 #{ENV['GNDMS_SOURCE']}/dev-bin/chownSlice.sh #{ENV['GNDMS_SHARED']}"
end

desc 'Test the c3 data-provider setup'
task 'c3grid-dp-test' => ['gndms:gndmc:run-staging-test']

  
task 'c3grid-dp-post-deploy-test' do
    host = `hostname`.chomp
    dn = `grid-proxy-info -identity`
    dn = dn.chomp
    if (ENV['GNDMS_SFR'] == nil)
      prop = 'etc/sfr/dummy-sfr.properties'
    else 
      prop = ENV['GNDMS_SFR']
    end
    # Yes, this is a hack
    cp = deployedJars()
    cp << "#{ENV['GNDMS_SOURCE']}/lib/gndmc/gndms-gndmc-#{VERSION_NUMBER}.jar"
    print cp
    args = [ '-props', prop, 
             '-uri', 'https://' + host + ':8443/wsrf/services/gndms/GORFX',
             '-dn', dn
           ]
    Commands.java('de.zib.gndmc.GORFX.c3grid.ProviderStageInClient',  args, 
                  { :classpath => cp,
                    :properties => { "axis.ClientConfigFile" => ENV['GLOBUS_LOCATION'] + "/client-config.wsdd" } } )
end

# Main targets

desc 'Install missing dependencies (execute as globus user)'
task 'install-deps' => task('gndms:infra:install-deps')

desc 'Do a full rebuild'
task 'rebuild' => ['clean', 'clean-services', 'gndms:stuff:package', 'package-stubs', 'gndms:infra:package', 'install-deps', 'package-services', 'gndms:gndmc:package']

desc 'Do a full rebuild and deploy (execute as globus user)'
task 'redeploy' => ['clean', 'clean-services', 'gndms:stuff:package', 'package-stubs', 'gndms:infra:package', 'install-deps', 'package-DSpace', 'deploy-DSpace', 'package-GORFX', 'deploy-GORFX', 'gndms:gndmc:package'] do
end

desc 'Do a full release build and deploy (execute as globus user)'
task 'release-build' => ['gndms:update-release-info', 'redeploy' ]

desc 'Build all docs'
task 'build-docs' => ['apidocs']

desc 'Install and deploy a release build'
task 'install-distribution' => ['install-deps', 'deploy-DSpace', 'deploy-GORFX']

task 'fix-permissions' do
    system "#{ENV['GNDMS_SOURCE']}/scripts/internal/fix-permissions.sh"
end

task 'artifcats' => ['artifacts']

def hasPath?(path)
    return ( File.exists?(path) or File.symlink?(path) )
end

desc 'Guesses the previous installed version and removes it' 
task 'auto-clean' do
    puts 'Guessing installed version...'
    path = "#{ENV['GLOBUS_LOCATION']}/lib/"
    if( hasPath?( "#{path}gndms-shared-model.jar" ) )   
        puts 'GNDMS 0.2.8 detected.'
        cleanRev( '0.2.8' )
    elsif( hasPath?( "#{path}gndms-model-0.3.0.jar" ) )
        puts 'GNDMS 0.3.0 detected.'
        cleanRev( '0.3.0' )
    elsif( hasPath?( "#{path}gndms-model-0.3.2.jar" ) )
        puts 'GNDMS 0.3.2 detected.'
        cleanRev( '0.3.2' )
    elsif( hasPath?( "#{path}gndms-model-0.3.3.jar" ) )
        puts 'GNDMS 0.3.3 detected.'
        cleanRev( '0.3.3' )
    elsif( hasPath?( "#{path}gndms-model-0.3.4.jar" ) )
        puts 'GNDMS 0.3.4 detected.'
        cleanRev( '0.3.4' )
    else
        puts 'No previously installed version detected.'
   end
    puts 'About to remove old c3grid service directories (if existing)'
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/c3grid_DSpace" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/c3grid_DSpace" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/c3grid_GORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/c3grid_GORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/c3grid_WHORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/c3grid_WHORFX" )
    puts 'About to remove old gndms service directories (if existing)'
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/gndms_DSpace" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/gndms_DSpace" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/gndms_GORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/gndms_GORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/lib/gndms_WHORFX" )
    rm_rf( "#{ENV['GLOBUS_LOCATION']}/etc/gpt/packages/gndms_WHORFX" )
end


task 'clean-0.2.8' do
    cleanRev( '0.2.8' )
end

task 'clean-0.3.0' do
    cleanRev( '0.3.0' )
end

task 'clean-0.3.2' do
    cleanRev( '0.3.2' )
end

task 'clean-0.3.3' do
    cleanRev( '0.3.3' )
end

task 'clean-0.3.4' do
    cleanRev( '0.3.4' )
end


def cleanRev( version )
    IO.foreach( "#{ENV['GNDMS_SOURCE']}/buildr/#{version}/files" )  { |block|
        fn = eval( '"'+block+'"' ).chomp
        puts "Removing #{fn}" if( hasPath?( fn ) )  
        File.delete( fn ) if( hasPath?( fn ) )  
    }
end 



def nope()
     puts ''
     puts 'Please read the documentation on how to build, install, and deploy this software (doc/html or doc/md).'
     puts 'The installation of GNDMS is considerably easy, but not straightforward.'
     puts ''
end


def genScript( scriptName, runClass, args, jars, props )
    script = File.new(_( scriptName ), 'w')
    script.write( "#!/bin/bash\n\n" )
    script.write( "args=( " );
    args.each { |x| script.write( "\"#{x}\" " ) }
    script.write( ")\n\n")
    jars.each { |x| script.write( "cp=\"#{x}:$cp\"\n") } 
    script.write( "\n" )
    script.write( "props=( " )
    props.each { |k, v| script.write( "\"-D#{k}=#{v}\" " ) }
    script.write( ")\n\n" )
    script.write( "exec java -cp=$cp ${props[@]} #{runClass} ${args[@]}\n" )

    script.close
end

Rake::Task[:default].prerequisites.clear
Rake::Task[:release].prerequisites.clear
Rake::Task[:release].clear
Rake::Task[:install].prerequisites.clear
Rake::Task[:install].clear

task :default do nope() end
task :release do nope() end
task :install do nope() end



