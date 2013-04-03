<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Software License, Version 1.0 Copyright 2010 SRA International, Inc.
  ~ Copyright Notice.  The software subject to this notice and license includes both human
  ~ readable source code form and machine readable, binary, object code form (the "caBIG
  ~ Software").
  ~
  ~ Please refer to the complete License text for full details at the root of the project.
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>

<%--
 Page for displaying login dialogue
 @author Namrata Rane Last updated by: $Author: $
 @version $Rev: $
 --%>

<html>
<body>
<p>
    Please authenticate: it is required before making any changes
</p>

<form action="j_spring_security_check" method="POST">
    <table>
        <tr>
            <td>Username:</td>
            <td><input type='text' name='j_username'></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='j_password'></td>
        </tr>
        <tr>
            <td colspan='2'><input name="submit" type="submit" value="Log In"></td>
        </tr>
        <tr>
            <td colspan='2'><input name="reset" type="reset"></td>
        </tr>
    </table>
</form>
</body>
</html>