#$Id: Build.PL 17331 2012-07-11 14:49:27Z jensenma $
use Module::Build;

Module::Build->new
    ( dist_name     => 'jsonval',
      dist_version_from => 'lib/TCGA/BCR/JSONVal.pm',
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
