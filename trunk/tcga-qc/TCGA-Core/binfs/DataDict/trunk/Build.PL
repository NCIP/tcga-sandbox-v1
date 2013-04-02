#$Id: Build.PL 16458 2012-04-20 16:49:00Z jensenma $
use Module::Build;
# See perldoc Module::Build for details of how this works

Module::Build->new
    ( 
      dist_name     => 'DataDict',
      dist_version_from => 'bin/ddbuild.pl',
      dist_author => 'Mark A. Jensen',
      dist_abstract => 'Tools for maintaining a BCR data dictionary',
      license => 'restrictive',
      requires => {
	           'perl' => 5.008,
                   'XML::Twig' => 0,
                   'Scalar::Util' => 0,
		   'HTML::Strip' => 0
                  },
      build_requires => {
                         'Test::More' => 0,
			 'Test::Exception' => 0,
			 'Test::Warn' => 0,
			 'IPC::Run3' => 0
      }
    )->create_build_script;
