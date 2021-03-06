package togos.noise2.cache;

import junit.framework.TestCase;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.CacheDaDaDa_Da;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.IfDaDaDa_Da;
import togos.noise2.function.LessThanDaDaDa_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.ReduceOutDaDaDa_Da;
import togos.noise2.function.SimplexDaDaDa_Da;
import togos.noise2.function.X;
import togos.noise2.function.Y;
import togos.noise2.function.Z;
import togos.noise2.lang.Expression;
import togos.noise2.lang.ScriptError;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.rewrite.CacheRewriter;
import togos.noise2.rewrite.ConstantFolder;

public class CacheRewriterTest extends TestCase
{
	public void testRewrite() throws ScriptError {
		String src = "simplex + 2 * simplex + 2 + 3 + 4 * simplex + 4 * simplex";
		
		TNLCompiler comp = new TNLWorldGeneratorCompiler();
		
		Object o = comp.compile(src, "test source");
		CacheRewriter cw = new CacheRewriter(SoftCache.getInstance());
		cw.initCounts((Expression)o);
		// cw.dumpCounts(System.err);

		o = cw.rewrite(o);
		
		assertTrue( o instanceof AddOutDaDaDa_Da );
		Object[] dse = ((ReduceOutDaDaDa_Da)o).directSubExpressions();
		assertTrue( dse[0] instanceof CacheDaDaDa_Da );
		assertTrue( dse[1] instanceof MultiplyOutDaDaDa_Da );
		assertTrue( dse[2] instanceof Constant_Da );
		assertTrue( dse[3] instanceof Constant_Da );
		assertTrue( dse[4] instanceof CacheDaDaDa_Da );
		assertTrue( dse[5] instanceof CacheDaDaDa_Da );
		// the simplex in the (4 * simplex)
		assertTrue( ((ReduceOutDaDaDa_Da)((CacheDaDaDa_Da)dse[4]).next).directSubExpressions()[1] instanceof CacheDaDaDa_Da );
	}

	public void testRewrite2() throws ScriptError {
		String src = "if( simplex * 4 + simplex + y < 0, 1, simplex * 4 + simplex + y < 1, 2, 3 )";
		
		TNLCompiler comp = new TNLWorldGeneratorCompiler();
		
		Object o = comp.compile(src, "test source");
		CacheRewriter cw = new CacheRewriter(SoftCache.getInstance());
		cw.initCounts((Expression)o);
		// cw.dumpCounts(System.err);

		o = cw.rewrite(o);
		
		assertTrue( o instanceof IfDaDaDa_Da );
		Object[] dse = ((IfDaDaDa_Da)o).directSubExpressions();
		
		assertTrue( dse[0] instanceof LessThanDaDaDa_Da );
		Object[] dse0 = ((LessThanDaDaDa_Da)dse[0]).directSubExpressions();
		assertTrue( dse0[0] instanceof CacheDaDaDa_Da );
		
		assertTrue( dse[2] instanceof LessThanDaDaDa_Da );
		Object[] dse2 = ((LessThanDaDaDa_Da)dse[0]).directSubExpressions();
		assertTrue( dse2[0] instanceof CacheDaDaDa_Da );
	}
	
	public void testRewriteNothing() throws ScriptError {
		String src = "1 * 2 + x + y + z + simplex";
		
		TNLCompiler comp = new TNLWorldGeneratorCompiler();
		
		Object o = comp.compile(src, "test source");
		CacheRewriter cw = new CacheRewriter(SoftCache.getInstance());
		cw.initCounts((Expression)o);
		// cw.dumpCounts(System.err);
		
		o = ConstantFolder.instance.rewrite(o);
		o = cw.rewrite(o);
		
		// Nothing should be cached because it is all trivial or
		// (in the case of the outermost expression) only appears once.
		
		assertTrue( o instanceof AddOutDaDaDa_Da );
		Object[] dse = ((AddOutDaDaDa_Da)o).directSubExpressions();
		
		assertTrue( dse[0] instanceof Constant_Da );
		assertTrue( dse[1] instanceof X );
		assertTrue( dse[2] instanceof Y );
		assertTrue( dse[3] instanceof Z );
		assertTrue( dse[4] instanceof SimplexDaDaDa_Da );
	}

	/*
	public void testRewrite3() throws ScriptError {
		String src = "if( ridge(0.0, 8.0, y + fractal(4, 8.0, 4.0, 2.0, 2.0, -1.0, simplex)) < 5.0 and "+
			"y > ridge(32.0, 96.0, xf(x, 0.0, z, (48.0 + fractal(8, 8.0, 4.0, 2.0, 2.0, -1.0, simplex)))) - 1.5, "+
			"3.0, " +
			"ridge(0.0, 8.0, y + fractal(4, 8.0, 4.0, 2.0, 2.0, -1.0, simplex)) < 3.0, " +
			"1.0, " +
			"ridge(0.0, 8.0, y + fractal(4, 8.0, 4.0, 2.0, 2.0, -1.0, simplex)) < 5.0, " +
			"3.0, " +
			"1.0 )";
		
		TNLCompiler comp = new TNLWorldGeneratorCompiler();
		
		Object o = comp.compile(src, "test source");
		CacheRewriter cw = new CacheRewriter(SoftCache.getInstance());
		cw.initCounts((Expression)o);
		cw.dumpCounts(System.err);
		
		o = cw.rewrite(o);
		
		System.err.println(o.toString());
	}
	*/
}
