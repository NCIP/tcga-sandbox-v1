package TCGA::ThrottledUserAgent;
use base 'LWP::UserAgent';
use Time::HiRes qw(usleep);

sub new {
  my ($class, %args) = @_;
  my $wait = delete $args{wait};
  my $self = bless $class->SUPER::new(%args), $class;
  $self->{_wait} = $wait;
  return $self;
}

sub get {
  my ($self, @args) = @_;
  $self->wait && usleep($self->wait);
  return $self->SUPER::get(@args);
}

sub wait { 
  my $self = shift;
  if (@_) {
    return $self->{_wait} = shift;
  }
  return $self->{_wait};
}
1;
