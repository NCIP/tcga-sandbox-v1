use Module::Build;
# See perldoc Module::Build for details of how this works
my $class = Module::Build->subclass
    (
     class => 'TCGA::DBRO::Build',
     code => q{
          sub process_ref_files {
	      # this copies everything in ./refdata to blib/refdata
	      my $self = shift;
	      opendir my $d, "refdata" or die "refdata : $!";
	      my @files = grep { -f "refdata/$_" }  readdir $d;;
	      $self->copy_if_modified(from => "refdata/$_", to_dir => "blib") for @files;
          }
         sub process_sql_files {
             my $self = shift;
             opendir my $d, "sql" or die "sql : $!";
             my @files = grep { -f "sql/$_" } readdir $d;
             $self->copy_if_modified(from => "sql/$_", to_dir => "blib") for @files;
         }
      }
    );
      
     

my $build = $class->new
    ( module_name     => 'TCGA::DBRO',
      dist_author   => 'Mark A. Jensen/Carl Schaefer',
      license       => 'restrictive',
      requires      => {
			'perl' => '5.8.0',
			'DBD::SQLite' => 0,
			'Scalar::Util' => 0,
                        'Tie::IxHash' => 0
			},
      recommends    => {
	  'TCGA::DBH' => 0
      },
      build_requires => {
	                 'Test::Exception' => 0
                        },
      build_recommends => {
			  'Test::Script' => 0
			 },
      install_path  => { refdata => $ENV{REFDATA_DIR} || "/etc/dbro/refdata"}

    );
$build->add_build_element('ref');
$build->add_build_element('sql');
$build->create_build_script;
