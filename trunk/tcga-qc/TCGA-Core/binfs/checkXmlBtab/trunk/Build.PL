use Module::Build;
# See perldoc Module::Build for details of how this works

Module::Build->new
    ( dist_name     => 'checkXmlBtab',
      dist_abstract => 'check BCR XML and biotab files',
      dist_version_from => 'lib/checkXmlBtab/Config.pm',
      dist_author => 'Mark A. Jensen',
      license => 'restrictive',
      requires      => { 'XML::Twig' => 0 },
      build_requires => { 'IPC::Run' => 0 }
    )->create_build_script;
