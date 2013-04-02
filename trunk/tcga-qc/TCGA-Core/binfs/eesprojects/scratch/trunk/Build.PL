#$Id: Build.PL 8840 2010-11-27 00:51:04Z jensenma $
# vi: ts=4
use	Module::Build;

Module::Build->new
	(	dist_name			=>	'jsonval',
		dist_version_from	=>	'lib/JSONVal/Configure.pm',
		dist_author			=>	'Mark A. Jensen',
		dist_abstractr		=>	'Validator for JSON-format BCR dashboard reports',
		license				=>	'restrictive',
		requires			=>	{
									'perl'			=>	5.008,
									'JSON'			=>	2.26,
									'Scalar::Util'	=>	0,
									'Data::Types'	=>	0
								},
		build_requires		=>	{
									'Test::More'	=>	0,
									'IPC::Run3'		=>	0
								}
	)->create_build_script;
