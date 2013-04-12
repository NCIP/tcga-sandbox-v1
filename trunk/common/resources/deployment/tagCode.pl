#
# Software License, Version 1.0 Copyright 2010 SRA International, Inc.
# Copyright Notice.  The software subject to this notice and license includes both human
# readable source code form and machine readable, binary, object code form (the "caBIG
# Software").
#
# Please refer to the complete License text for full details at the root of the project.
#

#########################################################################################
# tags all the tcga apps
# to run:  perl tagCode.pl miss_piggy 1234
#          and it will create a miss_piggy tag and do the commits with jira key APPS-1234


use strict;

my $tagName = shift @ARGV or die "First argument should be the tag name.  Second argument should be the jira issue number (without APPS).\n";
my $jiraKey = shift @ARGV or die "First argument should be the tag name.  Second argument should be the jira issue number (without APPS)\n";

my $svnUrl = 'https://ncisvn.nci.nih.gov/svn/tcgainformatics';
my @apps = ('tcga-parent', 'common', 'common-jaxb', 'common-web', 'qclive-test-data-generator', 'qclive', 'annotations', 'dam', 'datareports', 'uuid', 'databrowser', 'clide');

foreach my $app (@apps) {
  my $appCmd = "svn copy $svnUrl/$app/trunk $svnUrl/$app/tags/$tagName -m \"APPS-$jiraKey - tagging $app for $tagName\"";

  system($appCmd);
  print "Tagged $app\n";
}
