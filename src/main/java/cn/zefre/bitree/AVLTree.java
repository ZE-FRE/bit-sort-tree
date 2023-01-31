package cn.zefre.bitree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 平衡二叉树
 * 又叫AVL树、Self-Balancing Binary Search Tree、Height-Balanced Binary Search Tree
 *
 * AVL树与二叉排序树相比，增加了一个平衡因子，表示左右子树高度之差
 * 1：左子树比右子树高
 * 0：左右子树等高
 * -1：右子树比左子树高
 *
 * @author zefre
 */
public class AVLTree<E extends Comparable<E>> {

    /**
     * AVL树结点
     */
    static class AVLNode<E> {
        E data;

        /**
         * 树高度(或者说深度)
         */
        int height;

        AVLNode<E> left;

        AVLNode<E> right;

        /**
         * 构造函数，新增结点默认高度为1
         */
        AVLNode(E data) {
            this(data, 1);
        }

        AVLNode(E data, int height) {
            this.data = data;
            this.height = height;
        }
    }

    /**
     * AVL树根节点
     */
    private AVLNode<E> root;


    /**
     * 计算结点的高度
     *
     * @param node 结点
     * @author zefre
     * @return 结点高度
     */
    private int calculateHeight(AVLNode<?> node) {
        if (null == node) return 0;
        int leftHeight = null == node.left ? 0 : node.left.height;
        int rightHeight = null == node.right ? 0 : node.right.height;
        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * 计算结点的左右子树高度差
     *
     * @param node 结点
     * @author zefre
     * @return 结点的左右子树高度差
     */
    private int calculateGapOfLeftRight(AVLNode<?> node) {
        if (null == node) return 0;
        int leftHeight = null == node.left ? 0 : node.left.height;
        int rightHeight = null == node.right ? 0 : node.right.height;
        return leftHeight - rightHeight;
    }

    /**
     * AVL树查找结点
     *
     * @param elem 元素值
     * @author zefre
     * @return 元素存在返回对应结点，不存在返回null
     */
    public AVLNode<E> get(E elem) {
        if (null == elem) return null;
        AVLNode<E> target = root;
        while (null != target) {
            int compare = elem.compareTo(target.data);
            if (compare < 0)
                target = target.left;
            else if (compare > 0)
                target = target.right;
            else
                return target;
        }
        return null;
    }

    /**
     * 插入元素
     *
     * @param elem 待插入元素
     * @author zefre
     * @return elem为null或elem已存在返回false，否则返回true
     */
    public boolean add(E elem) {
        if (null == elem) return false;
        if (null == root) {
            root = new AVLNode<>(elem);
            return true;
        }
        return add(elem, root);
    }

    /**
     * 插入一个元素，保持树的平衡
     *
     * @param elem 待插入元素
     * @param node 结点
     * @author zefre
     * @return 插入成功返回true，若存在相同元素，返回false
     */
    private boolean add(E elem, AVLNode<E> node) {
        // 是否已插入
        boolean added = true;
        int compare = elem.compareTo(node.data);
        if (compare < 0) {
            if (null == node.left) // 找到插入位置，插入结点
                node.left = new AVLNode<>(elem);
            else
                added = add(elem, node.left);
        } else if (compare > 0) {
            if (null == node.right) // 找到插入位置，插入结点
                node.right = new AVLNode<>(elem);
            else
                added = add(elem, node.right);
        } else { // 结点已存在
            return false;
        }
        if (added) {
            node.height = calculateHeight(node);
            // 若插入后导致AVL树失衡，平衡AVL树
            if (Math.abs(calculateGapOfLeftRight(node)) > 1)
                balance(node);
        }
        return added;
    }

    /**
     * 删除结点
     *
     * @param elem 删除元素
     * @author zefre
     * @return 成功删除返回true，元素不存在返回false
     */
    public boolean remove(E elem) {
        if (null == elem || null == root) return false;
        return remove(elem, root, null);
    }

    /**
     * 删除结点，保持树平衡的平衡
     *
     * @param elem 删除元素
     * @param deletedNode 删除节点
     * @param parent 删除节点的双亲结点
     * @author zefre
     * @return 成功删除返回true，元素不存在返回false
     */
    private boolean remove(E elem, AVLNode<E> deletedNode, AVLNode<E> parent) {
        // 结点不存在，返回false
        if (null == deletedNode) return false;
        // 是否已删除
        boolean deleted = true;
        int compare = elem.compareTo(deletedNode.data);
        if (compare < 0)
            deleted = remove(elem, deletedNode.left, deletedNode);
        else if(compare > 0)
            deleted = remove(elem, deletedNode.right, deletedNode);
        else // 找到结点，删除结点
            removeNode(deletedNode, parent);
        if (deleted) {
            deletedNode.height = calculateHeight(deletedNode);
            // 若删除节点后导致AVL树失衡，平衡AVL树
            if (Math.abs(calculateGapOfLeftRight(deletedNode)) > 1)
                balance(deletedNode);
        }
        return deleted;
    }


    /**
     * 删除操作
     *
     * @param deletedNode 删除节点
     * @param parent 删除节点的双亲结点
     * @author zefre
     */
    private void removeNode(AVLNode<E> deletedNode, AVLNode<E> parent) {
        // 左右孩子都存在，转换为删除直接后继
        if (null != deletedNode.left && null != deletedNode.right) {
            parent = deletedNode;
            AVLNode<E> successor = deletedNode.right;
            while (null != successor.left) {
                parent = successor;
                successor = successor.left;
            }
            deletedNode.data = successor.data;
            // 删除直接后继
            remove(successor.data, successor, parent);
            return;
        }
        // 若只有左孩子或右孩子，直接用孩子结点代替
        AVLNode<E> child = null == deletedNode.left ? deletedNode.right : deletedNode.left;
        if (null != child) {
            deletedNode.data = child.data;
            deletedNode.left = child.left;
            deletedNode.right = child.right;
            child.left = null;
            child.right = null;
        } else {// 叶子结点
            if (deletedNode == root)
                root = null;
            else {
                if (deletedNode == parent.left)
                    parent.left = null;
                else
                    parent.right = null;
            }
        }
    }

    /**
     * 平衡子树
     *
     * @param node 待平衡子树根节点
     * @author zefre
     */
    private void balance(AVLNode<E> node) {
        int nodeBf = calculateGapOfLeftRight(node);
        if (nodeBf > 1) { // 左子树高
            /*
             * 取等于0是因为在删除时存在如下情况：
             *          10                     10                       6
             *        /    \     删除12       /      LL型，右单旋       /   \
             *       6      12   ---->       6        ---->          4     10
             *      / \                     / \                            /
             *     4   8                   4   8                          8
             */
            if (calculateGapOfLeftRight(node.left) >= 0) {
                // LL型，右单旋
                rightRotate(node);
            } else if (calculateGapOfLeftRight(node.left) < 0) {
                // LR型，先左旋，再右旋
                leftRotate(node.left);
                rightRotate(node);
            }
        } else if (nodeBf < -1) { // 右子树高
            if (calculateGapOfLeftRight(node.right) <= 0) {
                // RR型，左单旋
                leftRotate(node);
            } else if (calculateGapOfLeftRight(node.right) > 0) {
                // RL型，先右旋，再左旋
                rightRotate(node.right);
                leftRotate(node);
            }
        }
    }

    /**
     * 左旋
     *
     *            pivot                              right
     *          /      \                           /       \
     *       left     right       ---->          pivot    node
     *               /    \                     /    \
     *              ?    node                 left   ?
     *
     * @param pivot 旋转结点
     * @author zefre
     */
    private void leftRotate(AVLNode<E> pivot) {
        E pivotOriginalData = pivot.data;
        AVLNode<E> right = pivot.right;
        pivot.data = right.data;
        pivot.right = right.right;
        right.data = pivotOriginalData;
        right.right = right.left;
        right.left = pivot.left;
        pivot.left = right;
        pivot.left.height = calculateHeight(pivot.left);
        pivot.height = calculateHeight(pivot);
    }

    /**
     * 右旋
     *
     *            pivot                          left
     *          /      \                       /      \
     *       left     right    ---->        node     pivot
     *      /   \                                   /    \
     *   node    ?                                 ?    right
     *
     * @param pivot 旋转结点
     * @author zefre
     */
    private void rightRotate(AVLNode<E> pivot) {
        E pivotOriginalData = pivot.data;
        AVLNode<E> left = pivot.left;
        pivot.data = left.data;
        pivot.left = left.left;
        left.data = pivotOriginalData;
        left.left = left.right;
        left.right = pivot.right;
        pivot.right = left;
        pivot.right.height = calculateHeight(pivot.right);
        pivot.height = calculateHeight(pivot);
    }

    /**
     * 层序遍历AVL树
     *
     * @author zefre
     * @return 遍历结果集
     */
    public List<E> sequence() {
        List<AVLNode<E>> sequenceList = new ArrayList<>();
        if (null != root)
            sequenceList.add(root);
        for (int i = 0; i < sequenceList.size(); i++) {
            AVLNode<E> node = sequenceList.get(i);
            if (null != node.left)
                sequenceList.add(node.left);
            if (null != node.right)
                sequenceList.add(node.right);
        }
        return sequenceList.stream().map(avlNode -> avlNode.data).collect(Collectors.toList());
    }

}
