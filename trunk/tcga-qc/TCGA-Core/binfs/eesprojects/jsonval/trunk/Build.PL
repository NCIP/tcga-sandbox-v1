#$Id: Build.PL 18048 2013-01-24 16:51:56Z snyderee $
use Module::Build;

Module::Build->new
    ( dist_name     => 'jsonval',
      dist_version_from => 'lib/JSONVal/Configure.pm',
      dist_author => 'Mark A. Jensen',
      dist_abstract => 'Validator for JSON-format BCR dashboard reports',
      license         => 'restrictive',
      requires => {
		   'perl' => 5.008,
		   'JSON' => 2.26,
		   'Scalar::Util' => 0
		  },
      build_requires => {
			 'Test::More' => 0,
			 'IPC::Run3' => 0
			 }
    )->create_build_script;
