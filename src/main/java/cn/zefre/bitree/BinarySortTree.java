package cn.zefre.bitree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 二叉排序树(二叉搜索树)
 * 元素不允许重复
 *
 * @author zefre
 */
public class BinarySortTree<E extends Comparable<E>> {

    /**
     * 二叉排序树结点
     */
    static class BSTNode<E> {
        /**
         * 结点值
         */
        E data;
        /**
         * 左结点
         */
        BSTNode<E> left;
        /**
         * 右节点
         */
        BSTNode<E> right;

        BSTNode(E data) {
            this.data = data;
        }
    }

    /**
     * 根节点
     */
    private BSTNode<E> root;

    /**
     * 递归方式查找元素对应的结点
     * 若找到元素，返回对应结点，否则返回null
     *
     * @param elem 查找元素
     * @author zefre
     * @return 元素对应结点
     */
    public BSTNode<E> get(E elem) {
        if (null == elem) return null;
        return get(elem, root);
    }

    /**
     * 递归查找元素对应结点
     *
     * @param elem 查找元素
     * @param node 查找结点
     * @author zefre
     * @return 找到返回对应结点，否则返回null
     */
    private BSTNode<E> get(E elem, BSTNode<E> node) {
        // 未查找到元素结点
        if (null == node) return null;
        int compare = elem.compareTo(node.data);
        if (compare < 0) // 小于当前结点，在左子树查找
            return get(elem, node.left);
        else if (compare > 0) // 大于当前结点，在右子树查找
            return get(elem, node.right);
        else // 找到了对应元素结点
            return node;
    }

    /**
     * 插入元素
     *
     * @param elem 待插入元素
     * @author zefre
     * @return 插入成功返回true，元素存在则返回false
     */
    public boolean add(E elem) {
        if (null == elem) return false;
        BSTNode<E> newNode = new BSTNode<>(elem);
        if (null == root) {
            root = newNode;
            return true;
        }
        /*
         * 定位插入结点的位置
         */
        BSTNode<E> parent = null;
        BSTNode<E> node = root;
        while (node != null) {
            parent = node;
            int compare = elem.compareTo(node.data);
            if (compare < 0)
                node = node.left;
            else if (compare > 0)
                node = node.right;
            else // 结点已存在
                return false;
        }
        // 插入结点
        if (elem.compareTo(parent.data) < 0)
            parent.left = newNode;
        else
            parent.right = newNode;
        return true;
    }

    /**
     * 插入元素(递归方式)
     *
     * @param elem 待插入元素
     * @author zefre
     */
    public void insert(E elem) {
        if (null == elem) return;
        root = insert(elem, root);
    }

    /**
     * 插入元素(递归方式)
     *
     * @param elem 待插入元素
     * @param node 结点
     * @author zefre
     * @return 原结点
     */
    private BSTNode<E> insert(E elem, BSTNode<E> node) {
        if (null == node)
            return new BSTNode<>(elem);
        int compare = elem.compareTo(node.data);
        if (compare < 0)
            node.left = insert(elem, node.left);
        else if (compare > 0)
            node.right = insert(elem, node.right);
        return node;
    }


    /**
     * 删除结点
     *
     * @param elem 待删除元素
     * @author zefre
     * @return 成功删除返回true，元素不存在返回false
     */
    public boolean remove(E elem) {
        if (null == elem || null == root) return false;
        /*
         * 寻找删除结点和它的双亲
         */
        BSTNode<E> parent = null;
        BSTNode<E> deletedNode = root;
        while (null != deletedNode) {
            int compare = elem.compareTo(deletedNode.data);
            // 找到删除结点
            if (compare == 0)
                break;
            parent = deletedNode;
            if (compare < 0)
                deletedNode = deletedNode.left;
            else
                deletedNode = deletedNode.right;
        }
        // 结点不存在
        if (null == deletedNode) return false;
        // 删除结点
        removeNode(deletedNode, parent);
        return true;
    }

    /**
     * 被删除结点分以下几种情况：
     * ① 叶子节点
     * ② 只有左子树
     * ③ 只有右子树
     * ④ 左右子树都存在
     * 左右子树都存在时可以找它中序遍历下的直接前驱或直接后继来替代它
     *
     * @param deletedNode 删除结点
     * @param parent 删除结点的双亲
     * @author zefre
     */
    private void removeNode(BSTNode<E> deletedNode, BSTNode<E> parent) {
        // 左右孩子都存在，转换为删除直接后继
        if (null != deletedNode.left && null != deletedNode.right) {
            parent = deletedNode;
            BSTNode<E> successor = deletedNode.right;
            while (null != successor.left) {
                parent = successor;
                successor = successor.left;
            }
            deletedNode.data = successor.data;
            deletedNode = successor;
        }
        // 若只有左孩子或右孩子，直接用孩子结点代替
        BSTNode<E> child = null == deletedNode.left ? deletedNode.right : deletedNode.left;
        if (null != child) {
            deletedNode.data = child.data;
            deletedNode.left = child.left;
            deletedNode.right = child.right;
            child.left = null;
            child.right = null;
            return;
        }
        // 删除结点是叶子结点
        if (deletedNode == root) {
            root = null;
        } else {
            if (deletedNode == parent.left)
                parent.left = null;
            else
                parent.right = null;
        }
    }

    /**
     * 先序遍历
     *
     * @author zefre
     * @return 先序遍历结果集
     */
    public List<E> preOrder() {
        List<BSTNode<E>> orderList = new ArrayList<>();
        preOrder(root, orderList);
        return orderList.stream().map(node -> node.data).collect(Collectors.toList());
    }

    /**
     * 中序遍历
     *
     * @author zefre
     * @return 中序遍历结果集
     */
    public List<E> inOrder() {
        List<BSTNode<E>> orderList = new ArrayList<>();
        inOrder(root, orderList);
        return orderList.stream().map(node -> node.data).collect(Collectors.toList());
    }

    /**
     * 后序遍历
     *
     * @author zefre
     * @return 后序遍历结果集
     */
    public List<E> postOrder() {
        List<BSTNode<E>> orderList = new ArrayList<>();
        postOrder(root, orderList);
        return orderList.stream().map(node -> node.data).collect(Collectors.toList());
    }

    /**
     * 层序遍历
     *
     * @author zefre
     * @return 层序遍历结果集
     */
    public List<E> sequence() {
        List<BSTNode<E>> sequenceList = new ArrayList<>();
        if (null != root)
            sequenceList.add(root);
        for (int i = 0; i < sequenceList.size(); i++) {
            BSTNode<E> node = sequenceList.get(i);
            if (null != node.left)
                sequenceList.add(node.left);
            if (null != node.right)
                sequenceList.add(node.right);
        }
        return sequenceList.stream().map(bstNode -> bstNode.data).collect(Collectors.toList());
    }


    /**
     * 先序遍历二叉树
     *
     * @param node 遍历结点
     * @param preOrderList 先序遍历结果集合
     * @author zefre
     */
    private void preOrder(BSTNode<E> node, List<BSTNode<E>> preOrderList) {
        if (null == node) return;
        preOrderList.add(node);
        preOrder(node.left, preOrderList);
        preOrder(node.right, preOrderList);
    }

    /**
     * 中序遍历二叉树
     *
     * @param node 遍历结点
     * @param inOrderList 中序遍历结果集
     * @author zefre
     */
    private void inOrder(BSTNode<E> node, List<BSTNode<E>> inOrderList) {
        if (null == node) return;
        inOrder(node.left, inOrderList);
        inOrderList.add(node);
        inOrder(node.right, inOrderList);
    }

    /**
     * 后序遍历二叉树
     *
     * @param node 遍历结点
     * @param postOrderList 后序遍历结果集
     * @author zefre
     */
    private void postOrder(BSTNode<E> node, List<BSTNode<E>> postOrderList) {
        if (null == node) return;
        postOrder(node.left, postOrderList);
        postOrder(node.right, postOrderList);
        postOrderList.add(node);
    }

}
