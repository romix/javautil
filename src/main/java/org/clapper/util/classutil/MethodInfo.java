package org.clapper.util.classutil;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

/**
 * Holds information about a method within a class.
 *
 * @see ClassInfo
 */
public class MethodInfo implements Comparable<MethodInfo>
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private int access = 0;
    private String name = null;
    private String description = null;
    private String signature = null;
    private String[] exceptions = null;
	private ClassInfo declaringClass = null;
	private List<AnnotationInfo>      annotations = new ArrayList<AnnotationInfo>(); 

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Create a new, empty <tt>MethodInfo</tt> object.
     */
    public MethodInfo()
    {
    }

    /**
     * Create and initialize a new <tt>MethodInfo</tt> object.
     *
     * @param access      method access modifiers, etc.
     * @param name        method name
     * @param description method description
     * @param signature   method signature
     * @param exceptions  list of thrown exceptions (by name)
     */
    public MethodInfo(int access,
                      String name,
                      String description,
                      String signature,
                      String[] exceptions,
					  ClassInfo declaringClass)
    {
        this.access = access;
        this.name = name;
        this.description = description;
		if (signature != null && signature.startsWith("(") && name != null)
			signature = name + signature;
        this.signature = signature;
        this.exceptions = exceptions;
		this.declaringClass = declaringClass;
    }

    /*----------------------------------------------------------------------*\
                            Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Get the access modifiers for this method.
     *
     * @return the access modifiers, or 0 if none are set.
     */
    public int getAccess()
    {
        return access;
    }

    /**
     * Get the method name.
     *
     * @return the method name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the method description, if any.
     *
     * @return the method description, or null
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the method's signature, if any.
     *
     * @return the method signature, or null.
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * Get the class names of the thrown exceptions
     *
     * @return the names of the thrown exceptions, or null.
     */
    public String[] getExceptions()
    {
        return exceptions;
    }
	
    /**
     * Get the method's declaring class.
     *
     * @return the method declaring class, or null.
     */
    public ClassInfo getDeclaringClass()
    {
        return declaringClass;
    }
	
	public void addAnnotation(AnnotationInfo annotation)
	{
		annotations.add(annotation);
	}

	public List<AnnotationInfo> getAnnotations()
	{
		return annotations;
	}
	
	public boolean isAnnotationPresent(String desc) {
		AnnotationInfo ann = new AnnotationInfo();
		ann.setClassName(desc);
		return annotations.contains(ann);
	}

	public boolean isAnnotationPresent(Class<?> clazz) {
		AnnotationInfo ann = new AnnotationInfo();
		ann.setClassName(clazz.getName());
		return annotations.contains(ann);
	}

    /**
     * Get the hash code. The hash code is based on the field's signature.
     *
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return signature.hashCode() + declaringClass.getClassName().hashCode();
    }

    /**
     * Compare this object and another <tt>MethodInfo</tt> object. The two
     * objects are compared by their signature fields.
     *
     * @param other  the other object
     *
     * @return a negative integer, zero, or a positive integer, as this
     *         object is less than, equal to, or greater than the specified
     *         object.
     */
    public int compareTo(MethodInfo other)
    {
        return this.signature.compareTo(other.signature);
    }

    /**
     * Compare this object to another one for equality. If the other
     * object is a <tt>MethodInfo</tt> instance, the two will be compared by
     * signature.
     *
     * @param other  the other object
     *
     * @return <tt>true</tt> if <tt>other</tt> is a <tt>MethodInfo</tt>
     *         object and it has the same signature as this object,
     *         <tt>false</tt> otherwise.
     */
    public boolean equals(Object other)
    {
        boolean result;

        if (other instanceof MethodInfo)
			result = compareTo((MethodInfo) other) == 0
					&& declaringClass.getClassName().equals(
							((MethodInfo) other).declaringClass.getClassName());
        else
            result = false;

        return result;
    }

    /**
     * Return a string representation of the method. Currently, the string
     * representation is just the method's signature, or the name if the
     * signature is null.
     *
     * @return a string representation
     */
    public String toString()
    {
        if(declaringClass != null)
        	return declaringClass.getClassName()+"."+((signature != null) ? signature : name);
        else
        	return (signature != null) ? signature : name;
    }
}
