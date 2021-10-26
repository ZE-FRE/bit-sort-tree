package cn.zefre.bitree;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 二叉搜索树测试
 *
 * @author zefre
 */
public class BinarySortTreeTest {

    private static final BinarySortTree<Integer> bitSortTree = new BinarySortTree<>();

    /*
     * 初始化一颗二叉排序树
     *            34
     *          /    \
     *        24      39
     *      /   \       \
     *    18    28      64
     *   /     /  \    /  \
     *  6     27  32  42  66
     *   \              \
     *   14             48
     */
    static {
        Integer[] nums = new Integer[]{34, 24, 18, 39, 28, 6, 64, 14, 42, 48, 32, 66, 27};
        for (Integer num : nums) {
            bitSortTree.insert(num);
        }
    }

    @Test
    public void testPreOrder() {
        List<Integer> nums = bitSortTree.preOrder();
        List<Integer> expected = Arrays.asList(34, 24, 18, 6, 14, 28, 27, 32, 39, 64, 42, 48, 66);
        Assert.assertEquals(expected, nums);
    }

    @Test
    public void testInOrder() {
        List<Integer> nums = bitSortTree.inOrder();
        List<Integer> expected = Arrays.asList(6, 14, 18, 24, 27, 28, 32, 34, 39, 42, 48, 64, 66);
        Assert.assertEquals(expected, nums);
    }

    @Test
    public void testPostOrder() {
        List<Integer> nums = bitSortTree.postOrder();
        List<Integer> expected = Arrays.asList(14, 6, 18, 27, 32, 28, 24, 48, 42, 66, 64, 39, 34);
        Assert.assertEquals(expected, nums);
    }

    @Test
    public void testGet() {
        BinarySortTree.BSTNode<Integer> bstNode = bitSortTree.get(42);
        Assert.assertEquals(Integer.valueOf(42), bstNode.data);
        Assert.assertNull(bstNode.left);
        Assert.assertEquals(Integer.valueOf(48), bstNode.right.data);
    }

    @Test
    public void testAdd() {
        BinarySortTree<Integer> bsTree = new BinarySortTree<>();
        for (Integer num : Arrays.asList(34, 24, 18, 39, 28, 6, 64, 14, 42, 48, 32, 66, 27)) {
            bsTree.add(num);
        }
        List<Integer> expected = Arrays.asList(34, 24, 39, 18, 28, 64, 6, 27, 32, 42, 66, 14, 48);
        Assert.assertEquals(expected, bsTree.sequence());

        // 插入已存在元素
        Assert.assertFalse(bsTree.add(27));
    }

    @Test
    public void testRemove() {
        BinarySortTree<Integer> binarySortTree = new BinarySortTree<>();
        for (Integer num : bitSortTree.sequence()) {
            binarySortTree.add(num);
        }

        /*
         * 初始二叉排序树
         *            34
         *          /    \
         *        24      39
         *      /   \       \
         *    18    28      64
         *   /     /  \    /  \
         *  6     27  32  42  66
         *   \              \
         *   14             48
         */

        Assert.assertFalse(binarySortTree.remove(null));
        // 删除不存在结点
        Assert.assertFalse(binarySortTree.remove(100));

        // 删除根结点34，转换为删除直接后继39
        binarySortTree.remove(34);
        /*
         *            34                                            39
         *          /    \                                       /      \
         *        24      39                                   24        64
         *      /   \       \    删除39：用右子树64直接替换      /  \      /  \
         *    18    28      64          ---->               18   28    42  66
         *   /     /  \    /  \                            /    /  \    \
         *  6     27  32  42  66                          6    27  32   48
         *   \              \                              \
         *   14             48                             14
         */

        // 删除叶子结点，直接删除
        binarySortTree.remove(66);
        /*
         *             39
         *          /      \
         *        24        64
         *       /  \      /
         *     18   28    42
         *    /    /  \    \
         *   6    27  32   48
         *    \
         *     14
         */

        // 删除结点无左子树，用右子树直接代替
        binarySortTree.remove(42);
        /*
         *             39                           39
         *          /      \                     /      \
         *        24        64                 24        64
         *       /  \      /                  /  \      /
         *     18   28    42        ---->    18   28   48
         *    /    /  \    \                /    /  \
         *   6    27  32   48              6    27  32
         *    \                             \
         *     14                           14
         */

        // 删除结点无右子树
        binarySortTree.remove(18);
        /*
         *             39                          39
         *          /      \                    /      \
         *        24        64                24        64
         *       /  \      /                /    \      /
         *     18   28    48        ---->  6     28    48
         *    /    /  \                     \   /  \
         *   6    27  32                    14 27  32
         *    \
         *     14
         *
         */

        // 删除24，转换为删除直接后继27
        binarySortTree.remove(24);
        /*
         *            39                          39
         *         /      \                    /      \
         *       24        64                 27       64
         *     /    \      /                /    \     /
         *    6     28    48        ---->  6     28   48
         *     \   /  \                     \      \
         *     14 27  32                    14     32
         *
         */

        Assert.assertEquals(Arrays.asList(39, 27, 64, 6, 28, 48, 14, 32), binarySortTree.sequence());

    }
}
