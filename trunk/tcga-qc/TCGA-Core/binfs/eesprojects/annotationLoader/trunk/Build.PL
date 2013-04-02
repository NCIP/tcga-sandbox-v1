use Module::Build;

Module::Build->new (
	dist_name			=> 'dbGaPvalidator',
	dist_version_from	=> '',
	dist_author			=> 'Eric E. Snyder',
	dist_abstract		=> 'Validator for dbGaP submissions from DCC',
	license				=> 'restrictive',
	requires			=>	{	'perl'								=>	5.008,
								'File::Spec'						=>	3.33,
								'myUsage'							=>	0,
								'EESnyder'							=>	0,
								'TCGA'								=>	0,
								'Spreadsheet::ParseExcel::Simple'	=>	1.04,
								'LWP::UserAgent'					=>	6.02,
								'List::Compare'						=>	0.37,
								'Module::Build'						=>	0.38,
								'Term::ReadKey'						=>	2.30,
								'Config'							=>	0,
								'JSON::XS'							=>	2.32,
							},
	build_requires		=>	{	'Test::More'						=>	0.98,
							}
)->create_build_script;

