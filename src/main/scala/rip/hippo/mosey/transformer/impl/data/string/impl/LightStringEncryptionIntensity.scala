package rip.hippo.mosey.transformer.impl.data.string.impl

import org.objectweb.asm.tree._
import rip.hippo.mosey.asm.wrapper.ClassWrapper
import rip.hippo.mosey.transformer.impl.data.StringEncryptionTransformer
import rip.hippo.mosey.transformer.impl.data.string.StringEncryptionIntensity
import org.objectweb.asm.Opcodes._
import rip.hippo.mosey.util.MathUtil
import rip.hippo.mosey.util.asm.{MethodUtil, NumberInstructionUtil}

import scala.collection.mutable

/**
 * @author Hippo
 * @version 1.0.0, 7/31/20
 * @since 1.0.0
 */
final class LightStringEncryptionIntensity extends StringEncryptionIntensity {

  private val lookup = mutable.Map[String, String]()

  override def accept(classWrapper: ClassWrapper, parent: StringEncryptionTransformer): Unit = {
    val fieldNode = new FieldNode(ACC_PRIVATE | ACC_STATIC, parent.dictionary.generate(16), "Ljava/util/Map;", null, null)
    val methodNode = new MethodNode(ACC_PRIVATE | ACC_STATIC, parent.dictionary.generate(16), "(Ljava/lang/String;II)Ljava/lang/String;", null, null)

    lookup += (classWrapper.getName -> methodNode.name)

    def getField: FieldInsnNode = {
      new FieldInsnNode(GETSTATIC, classWrapper.getName, fieldNode.name, fieldNode.desc)
    }

    def append(int: Boolean): MethodInsnNode = {
      new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", if (int) "(I)Ljava/lang/StringBuilder;" else "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
    }

    val stringLocal = 0
    val key1Local = 1
    val key2Local = 2
    val mapLocal = 3
    val compoundLocal = 4
    val cachedLocal = 5
    val charArray = 6
    val loop = 7

    val decrypt = new LabelNode
    val startLoop = new LabelNode
    val finishLoop = new LabelNode

    methodNode.instructions = new InsnList() {
      add(getField)
      add(new InsnNode(DUP))
      add(new VarInsnNode(ASTORE, mapLocal))
      add(new InsnNode(MONITORENTER)) // synchronized (map)

      add(new TypeInsnNode(NEW, "java/lang/StringBuilder"))
      add(new InsnNode(DUP))
      add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"))
      add(new VarInsnNode(ALOAD, stringLocal))
      add(append(false))
      add(new VarInsnNode(ILOAD, key1Local))
      add(append(true))
      add(new VarInsnNode(ILOAD, key2Local))
      add(append(true))
      add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"))
      add(new VarInsnNode(ASTORE, compoundLocal)) // String compound = string + key1 + key2;

      add(getField)
      add(new VarInsnNode(ALOAD, compoundLocal))
      add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;"))
      add(new TypeInsnNode(CHECKCAST, "java/lang/String"))
      add(new VarInsnNode(ASTORE, cachedLocal)) // String cached = map.get(compound);

      add(new VarInsnNode(ALOAD, cachedLocal))
      add(new JumpInsnNode(IFNULL, decrypt))
      add(new VarInsnNode(ALOAD, cachedLocal))
      add(new VarInsnNode(ALOAD, mapLocal))
      add(new InsnNode(MONITOREXIT)) // free the map from the synchronized block
      add(new InsnNode(ARETURN)) // if (cached != null) return cached;

      add(decrypt)
      add(new VarInsnNode(ALOAD, stringLocal))
      add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C"))
      add(new VarInsnNode(ASTORE, charArray)) // char[] chars = string.toCharArray();

      add(new InsnNode(ICONST_0))
      add(new VarInsnNode(ISTORE, loop))
      add(startLoop)
      add(new VarInsnNode(ILOAD, loop))
      add(new VarInsnNode(ALOAD, charArray))
      add(new InsnNode(ARRAYLENGTH))
      add(new JumpInsnNode(IF_ICMPGE, finishLoop)) // for (int i = 0; i++;)

      add(new VarInsnNode(ALOAD, charArray))
      add(new VarInsnNode(ILOAD, loop))
      add(new VarInsnNode(ALOAD, charArray))
      add(new VarInsnNode(ILOAD, loop))
      add(new InsnNode(CALOAD))
      add(new VarInsnNode(ILOAD, key1Local))
      add(new VarInsnNode(ILOAD, key2Local))
      add(new InsnNode(IXOR))
      add(new InsnNode(ICONST_M1))
      add(new InsnNode(IXOR))
      add(new InsnNode(IXOR))
      add(new InsnNode(I2C))
      add(new InsnNode(CASTORE)) // chars[i] = (char) (chars[i] ^ ~(key1 ^ key2));

      add(new IincInsnNode(loop, 1))
      add(new JumpInsnNode(GOTO, startLoop)) // i++;

      add(finishLoop)
      add(new TypeInsnNode(NEW, "java/lang/String"))
      add(new InsnNode(DUP))
      add(new VarInsnNode(ALOAD, charArray))
      add(new MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V"))
      add(new VarInsnNode(ASTORE, charArray)) // String result = new String(chars);

      add(getField)
      add(new VarInsnNode(ALOAD, compoundLocal))
      add(new VarInsnNode(ALOAD, charArray))
      add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
      add(new InsnNode(POP)) // map.put(compound, result);

      add(new VarInsnNode(ALOAD, charArray))
      add(new VarInsnNode(ALOAD, mapLocal))
      add(new InsnNode(MONITOREXIT)) // free map from synchronized block
      add(new InsnNode(ARETURN)) // return result;
    }

    classWrapper.addMethod(methodNode)
    classWrapper.addField(fieldNode)

    MethodUtil.getClinit(classWrapper).getInstructions.insert(new InsnList() {
      add(new TypeInsnNode(NEW, "java/util/HashMap"))
      add(new InsnNode(DUP))
      add(new MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V"))
      add(new FieldInsnNode(PUTSTATIC, classWrapper.getName, fieldNode.name, fieldNode.desc))
    })


  }

  override def encrypt(ldcInsnNode: LdcInsnNode, classWrapper: ClassWrapper): InsnList = {
    val string = ldcInsnNode.cst.asInstanceOf[String]
    val key1 = MathUtil.generate(100000, 1000000)
    val key2 = MathUtil.generate(100000, 1000000)
    val encrypted = encrypt(string, key1, key2)
    val decryptMethod = lookup(classWrapper.getName)



    new InsnList() {
      add(new LdcInsnNode(encrypted))
      add(NumberInstructionUtil.getOptimizedInt(key2))
      add(NumberInstructionUtil.getOptimizedInt(key1))
      add(new InsnNode(SWAP))
      add(new InsnNode(DUP))
      add(new InsnNode(POP))
      add(new MethodInsnNode(INVOKESTATIC, classWrapper.getName, decryptMethod, "(Ljava/lang/String;II)Ljava/lang/String;"))
    }
  }

  private def encrypt(string: String, key1: Int, key2: Int): String = {
    val chars = string.toCharArray
    (0 until chars.length).foreach(i => chars(i) = (chars(i) ^ ~(key1 ^ key2)).asInstanceOf[Char])
    new String(chars)
  }
}
