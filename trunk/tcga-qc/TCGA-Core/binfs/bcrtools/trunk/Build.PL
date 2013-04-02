use Module::Build;

Module::Build->new
  (
   dist_name => 'BCRTools',
   dist_author => 'Mark A. Jensen',
   dist_version_from => 'lib/TCGA/BCR.pm',
   requires =>
   {
    'Spreadsheet::ParseExcel' => 0,
    'Spreadsheet::XLSX' => 0,
    'Set::Scalar' => 0,
    'Scalar::Util' => 0,
    'Tie::IxHash' => 0,
    'TCGA::BCR::JSONVal' => '1.4',
    'JSON' => 2.27
   },
   configure_requires => 
   {
    'Module::Build' => 0
   },
   build_requires => 
   {
    'Module::Build' => 0,
    'Test::More' => 0,
    'Test::Script' => 0
   }
  )->create_build_script;
