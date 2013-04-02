import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;
import org.apache.commons.io.IOUtils;


public class TestClient
{
	public static void main(String args[])
	{
		TestClient client = new TestClient();
		try
		{
			client.testSearch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void testSearch() throws Exception
	{
		//Application Service retrieval for secured system
		//ApplicationService appService = ApplicationServiceProvider.getApplicationService("userId","password");
		
		ApplicationService appService = ApplicationServiceProvider.getApplicationService();
		Collection<Class> classList = getClasses();
		for(Class klass:classList)
		{
			if (!Modifier.isAbstract(klass.getModifiers())){
				Object o = klass.newInstance();
				System.out.println("Searching for "+klass.getName());
				try
				{
					Collection results = appService.search(klass, o);
					for(Object obj : results)
					{
						printObject(obj, klass);
						break;
					}
				}catch(Exception e)
				{
					System.out.println(">>>"+e.getMessage());
				}
			}
		}
	}
	
	protected void printObject(Object obj, Class klass) throws Exception {
		System.out.println("Printing "+ klass.getName());
		
		Method[] methods = klass.getMethods();
		for(Method method:methods)
		{
			if(method.getName().startsWith("get") && !method.getName().equals("getClass"))
			{
				System.out.print("\t"+method.getName().substring(3)+":");
				Object val = method.invoke(obj, (Object[])null);
				if(val instanceof java.util.Set)
					System.out.println("size="+((Collection)val).size());
				else
					System.out.println(val);
			}
		}
	}


    protected Collection<Class> getClasses() throws Exception {

        JarFile file = null;

        Collection<Class> list = new ArrayList<Class>();
        int count = 0;

        for(File f:new File("lib").listFiles())
        {
            if(f.getName().endsWith("-beans.jar"))
            {
                try {
                    //noinspection IOResourceOpenedButNotSafelyClosed
                    file = new JarFile(f);
                    count++;
                } finally {
                    IOUtils.closeQuietly(file);
                }
            }
        }
        if(file == null) throw new Exception("Could not locate the bean jar");
        if(count>1) throw new Exception("Found more than one bean jar");

        Enumeration e = file.entries();
        while(e.hasMoreElements())
        {
            JarEntry o = (JarEntry) e.nextElement();
            if(!o.isDirectory())
            {
                String name = o.getName();
                if(name.endsWith(".class"))
                {
                    String klassName = name.replace('/', '.').substring(0, name.lastIndexOf('.'));
                    list.add(Class.forName(klassName));
                }
            }
        }
        return list;
    }
}