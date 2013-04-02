#$Id: Build.PL 7367 2010-06-28 15:21:53Z jensenma $
use Module::Build;
use lib './lib';
Module::Build->new
    ( dist_name     => 'repgen',
      dist_version_from => 'lib/RepGen/Configure.pm',
      dist_author   => 'Mark A. Jensen',
      dist_abstract => 'Flexible report generator for DCC binfs',
      license         => 'restrictive'
    )->create_build_script;
