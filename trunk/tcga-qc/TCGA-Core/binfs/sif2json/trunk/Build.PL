use Module::Build;

Module::Build->new
    ( module_name     => 'sif2json',
      dist_version_from => 'lib/sif2json.pm',
      dist_abstract   => 'sif2json.pl - Create JSON from BCR Excel SIF',
      author          => 'Mark A. Jensen',
      requires        => { 'JSON' => 0,
			   'Spreadsheet::ParseExcel' => 0,
			   'Tie::IxHash' => 0,
			   'DateTime::Format::Strptime' => 0 },
      build_requires  => { 'Test::Script' => 0 },
    )->create_build_script;
