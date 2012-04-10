/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M. Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.classutil;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.util.BitSet;
import java.util.Map;

/**
 * <p>An ASM <tt>ClassVisitor</tt> that records the appropriate class
 * information for a {@link ClassFinder} object.</p>
 *
 * <p>This class relies on the ASM byte-code manipulation library. If that
 * library is not available, this package will not work. See
 * <a href="http://asm.objectweb.org"><i>asm.objectweb.org</i></a>
 * for details on ASM.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @see ClassFinder
 */
class ClassInfoClassVisitor extends ClassDataPreservingClassVisitor
{
    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/


	private Map<String,ClassInfo> foundClasses;
    private File                  location;
    private ClassInfo             currentClass = null;
    private BitSet 				  currentlyProcessing = new BitSet(4);
    private BitSet 				  lastProcessing = null;
    private AnnotationInfo        currentAnnotation;
    private FieldInfo             currentField = null;
    private MethodInfo            currentMethod = null;

	protected static final int PROCESSING_FIELD = 1;
	protected static final int PROCESSING_METHOD = 2;
	protected static final int PROCESSING_CLASS = 3;
	protected static final int PROCESSING_PARAM = 4;

    /*----------------------------------------------------------------------*\
                               Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Constructor
     *
     * @param foundClasses  where to store the class information. The
     *                      {@link ClassInfo} records are stored in the map,
     *                      indexed by class name.
     * @param location      file (jar, zip) or directory containing classes
     *                      being processed by this visitor
     *
     */
    ClassInfoClassVisitor(Map<String,ClassInfo> foundClasses, File location)
    {
        this.foundClasses = foundClasses;
        this.location = location;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * "Visit" a class. Required by ASM <tt>ClassVisitor</tt> interface.
     *
     * @param version     class version
     * @param access      class access modifiers, etc.
     * @param name        internal class name
     * @param signature   class signature (not used here)
     * @param superName   internal super class name
     * @param interfaces  internal names of all directly implemented
     *                    interfaces
     */
    @Override
    public void visit(int      version,
                      int      access,
                      String   name,
                      String   signature,
                      String   superName,
                      String[] interfaces)
    {
        ClassInfo classInfo = new ClassInfo(name,
                                            superName,
                                            interfaces,
                                            access,
                                            location);
		classInfo.setBytecode(getBytecode());
        // Be sure to use the converted name from classInfo.getName(), not
        // the internal value in "name".
        foundClasses.put(classInfo.getClassName(), classInfo);
        currentClass = classInfo;
        currentlyProcessing.set(PROCESSING_CLASS);
    }

    /**
     * We get annotation values in this method, but have to track the current context.
     */
    @Override
    public void visit(String name, Object value)
    {
        if (currentAnnotation != null)
        {
            currentAnnotation.getParams().add(new AnnotationInfo.NameValue(name, value));
        }
    }

    /**
     * "Visit" a field.
     *
     * @param access      field access modifiers, etc.
     * @param name        field name
     * @param description field description
     * @param signature   field signature
     * @param value       field value, if any
     *
     * @return null.
     */
    @Override
    public FieldVisitor visitField(int access,
                                   String name,
                                   String description,
                                   String signature,
                                   Object value)
    {
        assert (currentClass != null);
        if (signature == null)
            signature = description + " " + name;
        currentClass.visitField(access, name, description,
                                       signature, value);
        currentlyProcessing.set(PROCESSING_FIELD);
        currentField = currentClass.getFields().lastElement();
        return this;

    }

    /**
     * "Visit" a method.
     *
     * @param access      field access modifiers, etc.
     * @param name        field name
     * @param description field description
     * @param signature   field signature
     * @param exceptions  list of exception names the method throws
     *
     * @return null.
     */
    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String description,
                                     String signature,
                                     String[] exceptions)
    {
        assert (currentClass != null);
        if (signature == null)
            signature = name + description;
        currentClass.visitMethod(access, name, description,
                                        signature, exceptions);
        currentlyProcessing.set(PROCESSING_METHOD);
        currentMethod = currentClass.getMethods().lastElement();
        return this;

    }

    /**
     * Get the location (the jar file, zip file or directory) containing
     * the classes processed by this visitor.
     *
     * @return where the class was found
     */
    public File getClassLocation()
    {
        return location;
    }

    @Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        assert (currentClass != null);
		currentClass.visitAnnotation(desc, visible);
		// are we processing anything currently? If not w'ere looking at another annotation on the same class element
        if (currentlyProcessing.nextSetBit(0) < 0)
        {
            if(lastProcessing!=null)
            {
                currentlyProcessing = lastProcessing;
            }
            else
            {
                return this;
            }
        }

        currentAnnotation = new AnnotationInfo();
        currentAnnotation.setClassName(getAnnotationClassName(desc));
        return this;
	}

//	@Override
//	public AnnotationVisitor visitAnnotation(String name, String desc) {
//        assert (currentClass != null);
//		return currentClass.visitAnnotation(name, desc);
//	}
//
//	@Override
//	public AnnotationVisitor visitAnnotationDefault() {
//        assert (currentClass != null);
//		return currentClass.visitAnnotationDefault();
//	}

	@Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible)
    {
        currentAnnotation = new AnnotationInfo();
        currentAnnotation.setClassName(getAnnotationClassName(desc));
        currentlyProcessing.set(PROCESSING_PARAM);
        return this;
    }

	public String getAnnotationClassName(String rawName)
    {
        return rawName.substring(1, rawName.length() - 1).replace('/', '.');
    }
	@Override
    public void visitEnd()
    {
        if (currentAnnotation != null)
        {

            if (currentlyProcessing.get(PROCESSING_CLASS))
            {
                currentClass.addAnnotation(currentAnnotation);
            }
            else if (currentlyProcessing.get(PROCESSING_FIELD))
            {
                currentField.addAnnotation(currentAnnotation);
            }
//            else if (currentlyProcessing.get(PROCESSING_PARAM))
//            {
//                currentParam.add(currentAnnotation);
//            }
            else if (currentlyProcessing.get(PROCESSING_METHOD))
            {
                currentMethod.addAnnotation(currentAnnotation);
            }
            currentAnnotation = null;
        }
        lastProcessing = (BitSet)currentlyProcessing.clone();
        currentlyProcessing.clear();
    }
}
