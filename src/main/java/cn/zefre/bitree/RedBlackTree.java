package cn.zefre.bitree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 红黑树
 * 一颗红黑树等价于一颗2-3-4树，一颗2-3-4树对应多颗红黑树
 * 2-3-4树是4阶B树
 * 红黑树的五大性质是2-3-4树等价变换为红黑树得来的
 *
 * 红黑树五大性质：
 * 1、结点是黑色或红色
 * 2、根结点是黑色
 * 3、叶子结点(nil、null结点)是黑色
 * 4、红色结点的两个子结点是黑色(即红色结点的双亲不能是红色结点)
 * 5、任一结点到它每一个叶子结点的路径包含相同数量的黑色结点(即红黑树的黑色平衡)
 *
 * @author zefre
 */
public class RedBlackTree<E extends Comparable<E>> {

    /**
     * 红色结点
     */
    private static final boolean RED = true;
    /**
     * 黑色结点
     */
    private static final boolean BLACK = false;

    /**
     * 红黑树结点
     */
    static class RBNode<E> {
        E data;
        /**
         * 结点颜色
         * true：红
         * false：黑
         */
        boolean color;

        RBNode<E> left;

        RBNode<E> right;
        /**
         * 双亲结点
         */
        RBNode<E> parent;

        RBNode(E data) {
            this(data, RED);
        }

        RBNode(E data, boolean color) {
            this.data = data;
            this.color = color;
        }
    }

    /**
     * 红黑树根结点
     */
    private RBNode<E> root;

    /**
     * 红黑树查找结点
     *
     * @param elem 查找元素
     * @author zefre
     * @return 元素存在返回对应结点，不存在返回null
     */
    public RBNode<E> get(E elem) {
        if (null == elem) return null;
        RBNode<E> target = root;
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
     * 红黑树插入新结点，新增结点默认为红色
     * 2-3-4树插入结点过程：
     * 在叶子结点插入，若插入后结点数等于4，则第二个结点向上分裂，若向上分裂导致上层结点等于4，则继续向上分裂
     *
     * 根据2-3-4树的插入，推导出红黑树插入的情形：
     * 1、红黑树为空，则新增一个黑色结点为根结点
     * 2、插入结点的双亲结点是黑色，则插入后不需要调整(变色、旋转)，直接返回
     * 3、插入结点的双亲结点是红色(则祖父结点一定是黑色)：
     *    3.1 叔叔结点也是红色，则只需变色，不用旋转
     *    3.2 叔叔结点是黑色(可能是null结点，null结点也是黑色)，分四种情况：LL型、LR型、RL型、RR型
     *
     * @param elem 插入元素
     * @author zefre
     * @return 插入成功返回true，元素存在返回false
     */
    public boolean add(E elem) {
        if (null == elem) return false;
        if (null == root) {
            root = new RBNode<>(elem, BLACK);
            return true;
        }
        /*
         * 寻找插入结点的位置
         */
        RBNode<E> parent = null;
        RBNode<E> node = root;
        while (node != null) {
            parent = node;
            int compare = elem.compareTo(node.data);
            if (compare < 0)
                node = node.left;
             else if (compare > 0)
                node = node.right;
             else
                 return false;
        }
        /*
         * 插入结点
         */
        RBNode<E> newNode = new RBNode<>(elem);
        newNode.parent = parent;
        if (elem.compareTo(parent.data) < 0)
            parent.left = newNode;
        else
            parent.right = newNode;
        // 调整
        adjustAfterInsertion(newNode);
        return true;
    }

    /**
     * 插入后调整红黑树
     * 需要调整的情况：
     * 1、 叔叔结点也是红色，则只需变色，不用旋转
     * 2、 叔叔结点是黑色(可能是null结点，null结点也是黑色)，分四种情况：
     *                                                       LL型
     *           grandpa(黑)                      parent(红)                                         parent(黑)
     *          //      \\          右旋         //      \\            parent变黑，grandpa变红        //      \\
     *      parent(红)  uncle(黑)   ---->   newNode(红)  grandpa(黑)          ---->             newNode(红)  grandpa(红)
     *       //                                            \\                                                 \\
     *   newNode(红)                                      uncle(黑)                                            uncle(黑)
     *
     *                                                       LR型
     *           grandpa(黑)                           grandpa(黑)
     *          //      \\        左旋变为LL型         //      \\
     *      parent(红)  uncle(黑)    ---->        newNode(红)  uncle(黑)    将parent(红)作为调整结点，重复LL型调整
     *          \\                                 //
     *         newNode(红)                    parent(红)
     *
     *                                                      RR型
     *          grandpa(黑)                           parent(红)                                         parent(黑)
     *         //       \\           左旋           //       \\          parent变黑，grandpa变红         //       \\
     *      uncle(黑)  parent(红)    ---->      grandpa(黑)  newNode(红)       ---->                grandpa(红)  newNode(红)
     *                   \\                       //                                                  //
     *                  newNode(红)            uncle(黑)                                          uncle(黑)
     *
     *                                                      RL型
     *           grandpa(黑)                          grandpa(黑)
     *          //      \\         右旋变为RR型       //       \\
     *      uncle(黑)  parent(红)    ---->        uncle(黑)  newNode(红)    将parent(红)作为调整结点，重复RR型调整
     *                  //                                      \\
     *               newNode(红)                             parent(红)
     *
     * @param node 调整结点
     * @author zefre
     */
    private void adjustAfterInsertion(RBNode<E> node) {
        while (node.parent.color == RED) {
            RBNode<E> parent = node.parent;
            RBNode<E> grandpa = parent.parent;
            RBNode<E> uncle = parent == grandpa.left ? grandpa.right : grandpa.left;
            if (colorOf(uncle) == RED) { // parent和uncle都是红色，只需变色
                grandpa.color = RED;
                parent.color = BLACK;
                uncle.color = BLACK;
                node = grandpa;
                if (node == root) {
                    root.color = BLACK;
                    return;
                }
            } else { // parent是红色，uncle是黑色，需要变色、旋转。这种情况经过下面代码一次调整，红黑树就平衡了
                if (parent == grandpa.left) {
                    if (node == parent.right) {  // LR型，先左旋变为LL型
                        node = parent;
                        leftRotate(parent);
                    }
                    rightRotate(grandpa);
                } else {
                    if (node == parent.left) { // RL型，先右旋变为RR型
                        node = parent;
                        rightRotate(parent);
                    }
                    leftRotate(grandpa);
                }
                // 变色(双亲变黑，祖父变红)
                // assert null != node.parent
                node.parent.color = BLACK;
                grandpa.color = RED;
            }
        }
    }

    private boolean colorOf(RBNode<E> node) {
        return null == node ? BLACK : node.color;
    }

    /**
     * 左旋
     *
     *            pivot                              right
     *          //    \\                           //     \\
     *       left     right       ---->          pivot    node
     *               //  \\                     //  \\
     *              ?    node                 left   ?
     *
     * @param pivot 旋转支点
     * @author zefre
     */
    private void leftRotate(RBNode<E> pivot) {
        RBNode<E> right = pivot.right;
        pivot.right = right.left;
        if (null != pivot.right)
            pivot.right.parent = pivot;
        if (null == pivot.parent) // pivot是根结点
            root = right;
        else if (pivot == pivot.parent.left)
            pivot.parent.left = right;
        else
            pivot.parent.right = right;
        right.parent = pivot.parent;
        right.left = pivot;
        pivot.parent = right;
    }

    /**
     * 右旋
     *
     *            pivot                          left
     *          //    \\                       //    \\
     *       left     right    ---->        node     pivot
     *      //  \\                                  //  \\
     *   node    ?                                 ?    right
     *
     * @param pivot 旋转支点
     * @author zefre
     */
    private void rightRotate(RBNode<E> pivot) {
        RBNode<E> left = pivot.left;
        pivot.left = left.right;
        if (null != pivot.left)
            pivot.left.parent = pivot;
        if (null == pivot.parent) // pivot是根结点
            root = left;
        else if (pivot == pivot.parent.left)
            pivot.parent.left = left;
        else
            pivot.parent.right = left;
        left.parent = pivot.parent;
        left.right = pivot;
        pivot.parent = left;
    }


    /**
     * 红黑树删除
     * 在2-3-4树中，删除结点最终都转换为删除叶子结点(最后一层，对应红黑树的倒数第一、二层)
     * 在红黑树中，删除结点最终都转换为删除叶子结点
     *
     * @param elem 删除元素
     * @author zefre
     * @return 成功删除返回true，元素不存在返回false
     */
    public boolean remove(E elem) {
        // 查找待删除结点
        RBNode<E> target = get(elem);
        // 元素不存在返回false
        if (null == target) return false;
        // 删除结点
        removeNode(target);
        return true;
    }

    /**
     * 删除结点
     *
     * @param deletedNode 删除结点
     * @author zefre
     */
    private void removeNode(RBNode<E> deletedNode) {
        // 左右孩子都存在，转换为删除直接后继
        if (null != deletedNode.left && null != deletedNode.right) {
            RBNode<E> successor = successor(deletedNode);
            deletedNode.data = successor.data;
            deletedNode = successor;
        }
        // 若只有左孩子或右孩子，转换为删除左孩子或右孩子结点
        RBNode<E> child = null == deletedNode.left ? deletedNode.right : deletedNode.left;
        if (null != child) {
            deletedNode.data = child.data;
            deletedNode = child;
        }
        // 只有一个根节点，直接删除
        if (deletedNode == root) {
            root = null;
            return;
        }
        // 删除结点是黑色，先调整再删除
        if (colorOf(deletedNode) == BLACK)
            adjustBeforeDeletion(deletedNode);
        // 删除结点
        if (deletedNode == deletedNode.parent.left)
            deletedNode.parent.left = null;
        else
            deletedNode.parent.right = null;
        deletedNode.parent = null;
    }

    /**
     * 删除结点调整红黑树
     *
     * 删除黑色叶子结点的两种情况：
     *  1.黑色兄弟结点存在红色孩子(一个或两个都行)
     *            parent(?)                                 parent(?)
     *          //      \\                                //      \\
     *      delete(黑) sibling(黑)       or          sibling(黑)  delete(黑)
     *                 //  \\                       //      \\
     *                ?   sib-child(红)       sib-child(红)   ?
     *
     *
     *  2.兄弟结点两个孩子都是黑色(null结点也是黑色)
     *          parent(?)                          parent(?)
     *        //      \\             or          //      \\
     *    delete(黑)  sibling(黑)             sibling(黑)  delete(黑)
     *
     * @param node 调整结点
     * @author zefre
     */
    private void adjustBeforeDeletion(RBNode<E> node) {
        while (node != root && colorOf(node) == BLACK) {
            // 双亲结点
            RBNode<E> parent = node.parent;
            // 兄弟结点
            RBNode<E> sibling;
            if (node == parent.left) {
                sibling = parent.right;
                if (colorOf(sibling) == RED) { // 兄弟结点是红色，绕双亲结点左旋，找到真正的兄弟结点
                    parent.color = RED;
                    sibling.color = BLACK;
                    leftRotate(parent);
                    sibling = parent.right;
                }
                // 兄弟结点两个孩子都是黑色，兄弟结点自损变红，然后将双亲结点作为调整结点
                if (colorOf(sibling.left) == BLACK && colorOf(sibling.right) == BLACK) {
                    sibling.color = RED;
                    node = parent;
                } else {
                    if (colorOf(sibling.right) == BLACK) { // 兄弟结点右孩子为null，绕兄弟结点右旋
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rightRotate(sibling);
                        sibling = parent.right;
                    }
                    // 绕双亲结点左旋，变色
                    sibling.color = parent.color;
                    sibling.right.color = BLACK;
                    parent.color = BLACK;
                    leftRotate(parent);
                    // 调整完毕，跳出循环，也可以直接用break
                    node = root;
                }
            } else { // 右边是对称的
                sibling = parent.left;
                if (colorOf(sibling) == RED) {
                    parent.color = RED;
                    sibling.color = BLACK;
                    rightRotate(parent);
                    sibling = parent.left;
                }
                if (colorOf(sibling.left) == BLACK && colorOf(sibling.right) == BLACK) {
                    sibling.color = RED;
                    node = parent;
                } else {
                    if (colorOf(sibling.left) == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        leftRotate(sibling);
                        sibling = parent.left;
                    }
                    sibling.color = parent.color;
                    sibling.left.color = BLACK;
                    parent.color = BLACK;
                    rightRotate(parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    /**
     * 获取中序遍历下结点的直接后继
     *
     * @param node 结点
     * @author zefre
     * @return 结点直接后继
     */
    private RBNode<E> successor(RBNode<E> node) {
        if (null == node)
            return null;
        else if (null != node.right) { // 结点右子树存在，则直接后继在右子树上
            RBNode<E> successor = node.right;
            while (successor.left != null)
                successor = successor.left;
            return successor;
        } else { // 结点右子树不存在，则直接后继是祖先结点，向上查找(找直接前驱的逆向过程)
            RBNode<E> parent = node.parent;
            RBNode<E> successor = node;
            while (parent != null && successor == parent.right) {
                successor = parent;
                parent = parent.parent;
            }
            return successor;
        }
    }


    /**
     * 层序遍历红黑树
     *
     * @author zefre
     * @return 遍历结果集
     */
    public List<E> sequence() {
        List<RBNode<E>> sequenceList = new ArrayList<>();
        if (null != root)
            sequenceList.add(root);
        RBNode<E> node;
        for (int i = 0; i < sequenceList.size(); i++) {
            node = sequenceList.get(i);
            if (null != node.left)
                sequenceList.add(node.left);
            if (null != node.right)
                sequenceList.add(node.right);
        }
        return sequenceList.stream().map(rbNode -> rbNode.data).collect(Collectors.toList());
    }

}
