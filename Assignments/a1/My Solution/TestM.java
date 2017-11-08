import org.junit.*;
import static org.junit.Assert.*;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class TestM {

    /* add your test code here */
	
	private OutputStream tempoutput;
	private PrintStream oldoutputmethod;
	private String separator;
	private String a;
	private String b;
	private String zero;
	
	private M m;
	private String arg;
	
    public TestM(){
    	tempoutput = new ByteArrayOutputStream();
    	separator = System.getProperty("line.separator");
    	a = "a"+separator;
    	b = "b"+separator;
    	zero = "zero"+separator;
    }
    
	@Before
	public void setUp(){
		oldoutputmethod = System.out;
		System.setOut(new PrintStream(tempoutput));
		m = new M();
		arg = new String();
	}
	
	@After
	public void tearDown(){
		System.setOut(oldoutputmethod);
		m = null;
		arg = null;
	}
	
	/*Node coverage but not edge coverage*/
	/*nc_1 nc_2 nc_3 nc_4*/
	@Test
	public void nc_1(){
		arg="";
		m.m(arg, 0);
		assertEquals(zero, tempoutput.toString());
	}
	
	@Test
	public void nc_2(){
		arg="1";
		m.m(arg, 0);
		assertEquals(a, tempoutput.toString());
	}
	
	@Test
	public void nc_3(){
		arg="12";
		m.m(arg, 0);
		assertEquals(b, tempoutput.toString());
	}
	
	@Test
	public void nc_4(){
		arg="123";
		m.m(arg, 0);
		assertEquals(b, tempoutput.toString());
	}
	
	/*Edge coverage but not edge-pair coverage*/
	/*ec_1 ec_2 ec_3 ec_4*/
	@Test
	public void ec_1(){
		arg="";
		m.m(arg, 0);
		assertEquals(zero, tempoutput.toString());
	}
	
	@Test
	public void ec_2(){
		arg="1";
		m.m(arg, 0);
		assertEquals(a, tempoutput.toString());
	}
	
	@Test
	public void ec_3(){
		arg="12";
		m.m(arg, 0);
		assertEquals(b, tempoutput.toString());
	}
	
	@Test
	public void ec_4(){
		arg="123";
		m.m(arg, 1);
		assertEquals(b, tempoutput.toString());
	}
	
	/*Test case for feasible edge-pair coverage 
	 * and feasible prime path coverage are the same*/
	/*epc_ppc_1 to epc_ppc_8*/
	@Test
	public void epc_ppc_1(){
		arg="";
		m.m(arg, 0);
		assertEquals(zero, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_2(){
		arg="1";
		m.m(arg, 0);
		assertEquals(a, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_3(){
		arg="12";
		m.m(arg, 0);
		assertEquals(b, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_4(){
		arg="123";
		m.m(arg, 0);
		assertEquals(b, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_5(){
		arg="";
		m.m(arg, 1);
		assertEquals(zero, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_6(){
		arg="1";
		m.m(arg, 1);
		assertEquals(a, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_7(){
		arg="12";
		m.m(arg, 1);
		assertEquals(b, tempoutput.toString());
	}
	
	@Test
	public void epc_ppc_8(){
		arg="123";
		m.m(arg, 1);
		assertEquals(b, tempoutput.toString());
	}
}

class M {
	public static void main(String [] argv){
		M obj = new M();
		if (argv.length > 0)
			obj.m(argv[0], argv.length);
	}
	
	public void m(String arg, int i) {
		int q = 1;
		A o = null;
		Impossible nothing = new Impossible();
		if (i == 0)
			q = 4;
		q++;
		switch (arg.length()) {
			case 0: q /= 2; break;
			case 1: o = new A(); new B(); q = 25; break;
			case 2: o = new A(); q = q * 100;
			default: o = new B(); break; 
		}
		if (arg.length() > 0) {
			o.m();
		} else {
			System.out.println("zero");
		}
		nothing.happened();
	}
}

class A {
	public void m() { 
		System.out.println("a");
	}
}

class B extends A {
	public void m() { 
		System.out.println("b");
	}
}

class Impossible{
	public void happened() {
		// "2b||!2b?", whatever the answer nothing happens here
	}
}
