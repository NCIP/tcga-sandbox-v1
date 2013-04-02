#$Id: Build.PL 17669 2012-09-17 02:30:47Z jensenma $
use Module::Build;
Module::Build->new
    ( dist_name     => 'TCGA-Barcodes',
      dist_author   => 'Mark A. Jensen',
      dist_version_from => 'lib/TCGA/Barcodes.pm',
      license => 'restrictive',
      configure_requires => {
	  'Module::Build' => 0.4
      },
      build_requires => {
	  'Module::Build' => 0
      },
      requires => {
	  'perl' => '5.8.0',
	  'DBD::Oracle' => 0,
	  'LWP::UserAgent' => 0,
          'JSON' => 0
      }
    )->create_build_script;
