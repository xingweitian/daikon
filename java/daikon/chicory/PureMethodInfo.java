package daikon.chicory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * The PureMethodInfo class is a subtype of DaikonVariableInfo used
 * for "variable types" which correspond to the values of pure method
 * invocations.
 */
public class PureMethodInfo extends DaikonVariableInfo
{

    /** The MethodInfo object for this pure method **/
    private MethodInfo minfo;

    /** An array containing the chosen variable parameters of this pure method,
     *  if this pure method has no args, then args.size() = 0
     */
    //TODO: Does it make sense to have args static?
    private DaikonVariableInfo[] args;
    
    
    //TODO: Should the initialization be entirely changed instead of creating
    //		separate initialization method? (LJT)
    
    public PureMethodInfo(String name, MethodInfo methInfo, boolean inArray)
    {
        this(name, methInfo, inArray, new DaikonVariableInfo[0]);
    }
    
    public PureMethodInfo(String name, MethodInfo methInfo, boolean inArray, DaikonVariableInfo[] args)
    {
    	super(name, inArray);
    	
    	assert methInfo.isPure(): "Method " + methInfo + " is not pure";
    	
    	minfo = methInfo;
    	
    	this.args = args;
    }
    

    /**
     * Invokes this pure method on the given parentVal.
     * This is safe because the method is pure!
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getMyValFromParentVal(Object parentVal)
    {
        @SuppressWarnings("nullness") // not a class initializer, so meth != null
        /*@NonNull*/ Method meth = (Method) minfo.member;
        boolean changedAccess = false;
        Object retVal;

        // we want to access all fields...
        if (!meth.isAccessible())
        {
            changedAccess = true;
            meth.setAccessible(true);
        }

        
       //TODO: What is occurring here? (LJT) 
        if (isArray)
        {
            // First check if parentVal is null or nonsensical
            if (parentVal == null || parentVal instanceof NonsensicalList)
            {
                retVal = NonsensicalList.getInstance();
            }
            else
            {
                List<Object> retList = new ArrayList<Object>();

                for (Object val : (List<Object>) parentVal) // unchecked cast
                {
                    if (val == null || val instanceof NonsensicalObject)
                    {
                        retList.add(NonsensicalObject.getInstance());
                    }
                    else
                    {
                    	Object[] params = new Object[args.length];
                 	   
                    	int i = 0;
                    	
                    	for (DaikonVariableInfo field : args) {
                    		params[i] = field.getMyValFromParentVal(parentVal);
                    		i++;
                    	}
                    	
                        retList.add(executePureMethod(meth, val, params));
                    }
                }

                retVal = retList;
            }
        }
        else
        {
            // First check if parentVal is null or nonsensical
            if (parentVal == null || parentVal instanceof NonsensicalObject)
            {
                retVal = NonsensicalObject.getInstance();
            }
            else
            {
            	Object[] params = new Object[args.length];
            	   
            	int i = 0;
            	
            	for (DaikonVariableInfo field : args) {
            		if(field.getMyValFromParentVal(parentVal) instanceof Runtime.PrimitiveWrapper) {
            			Runtime.PrimitiveWrapper x = (Runtime.PrimitiveWrapper) field.getMyValFromParentVal(parentVal);
            			params[i] = x.getJavaWrap();
            		} else {
            			params[i] = field.getMyValFromParentVal(parentVal);
            		}
            		i++;
            	}
            	
                retVal = executePureMethod(meth, parentVal, params);
            }

        }

        if (changedAccess)
        {
            meth.setAccessible(false);
        }

        return retVal;
    }
    
    //TODO: Need to make sure invoke works correctly... (LJT)

    private static Object executePureMethod(Method meth, Object objectVal, Object[] argVals)
    {
        Object retVal = null;
        try
        {
            // TODO is this the best way to handle this problem?
            // (when we invoke a pure method, Runtime.Enter should not be
            // called)
            Runtime.startPure();

            retVal = meth.invoke(objectVal, argVals);
           
            if (meth.getReturnType().isPrimitive())
                retVal = convertWrapper(retVal);
        }
        catch (IllegalArgumentException e)
        {
            throw new Error(e);
        }
        catch (IllegalAccessException e)
        {
            throw new Error(e);
        }
        catch (InvocationTargetException e)
        {
            retVal = NonsensicalObject.getInstance();
        }
        catch (Throwable e)
        {
            throw new Error(e);
        }
        finally
        {
            Runtime.endPure();
        }

        return retVal;
    }
     

    /**
     * Convert standard wrapped Objects (i.e., Integers) to Chicory wrappers (ie,
     * Runtime.IntWrap).  Should not be called if the Object was not auto-boxed
     * from from a primitive!
     */
    public static Object convertWrapper(Object obj)
    {
        if (obj == null || obj instanceof NonsensicalObject || obj instanceof NonsensicalList)
            return obj;

        if (obj instanceof Integer)
        {
            return new Runtime.IntWrap((Integer) obj);
        }
        else if (obj instanceof Boolean)
        {
            return new Runtime.BooleanWrap((Boolean) obj);
        }
        else if (obj instanceof Byte)
        {
            return new Runtime.ByteWrap((Byte) obj);
        }
        else if (obj instanceof Character)
        {
            return new Runtime.CharWrap((Character) obj);
        }
        else if (obj instanceof Float)
        {
            return new Runtime.FloatWrap((Float) obj);
        }
        else if (obj instanceof Double)
        {
            return new Runtime.DoubleWrap((Double) obj);
        }
        else if (obj instanceof Long)
        {
            return new Runtime.LongWrap((Long) obj);
        }
        else if (obj instanceof Short)
        {
            return new Runtime.ShortWrap((Short) obj);
        }
        else
        {
            // Not a primitive object (wrapper), so just keep it the same
            return obj;
        }

    }
    
    public VarKind get_var_kind() {
        return VarKind.FUNCTION;
    }

    /** Return the short name of the method as the relative name **/
    public String get_relative_name() {
        return minfo.method_name;
    }
}
