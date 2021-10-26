package cn.zefre.bitree;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;

/**
 * 红黑树测试
 *
 * @author zefre
 */
public class RedBlackTreeTest {

    @Test
    public void testAdd() {
        RedBlackTree<Integer> redBlackTree = new RedBlackTree<>();
        Assert.assertFalse(redBlackTree.add(null));

        redBlackTree.add(40);
        redBlackTree.add(70);
        /*
         *      40(黑)
         *          \\
         *          70(红)
         */

        redBlackTree.add(90);
        /*
         *      40(黑)                         70(黑)
         *          \\      绕40左旋，变色     //     \\
         *          70(红)     ---->       40(红)  90(红)
         *            \\
         *          90(红)
         */

        redBlackTree.add(20);
        /*
         *        70(黑)                        70(红)                        70(黑)
         *      //     \\         变色         //    \\      根节点变色       //     \\
         *    40(红)  90(红)      ---->      40(黑)  90(黑)    ---->       40(黑)  90(黑)
         *    //                            //                           //
         *  20(红)                        20(红)                        20(红)
         */

        redBlackTree.add(60);
        redBlackTree.add(80);
        redBlackTree.add(120);
        /*
         *           70(黑)
         *        //       \\
         *    40(黑)         90(黑)
         *   //    \\       //   \\
         * 20(红)  60(红)  80(红)  120(红)
         */

        redBlackTree.add(50);
        /*
         *    变色，40变红，20、60变黑
         *
         *           70(黑)
         *         //      \\
         *    40(红)         90(黑)
         *   //    \\       //   \\
         * 20(黑)  60(黑)  80(红)  120(红)
         *         //
         *       50(红)
         */

        redBlackTree.add(45);
        /*
         *           70(黑)                                             70(黑)
         *        //       \\                                        //       \\
         *    40(红)         90(黑)                               40(红)         90(黑)
         *   //    \\       //    \\      绕60右旋，变色           //   \\       //    \\
         * 20(黑)  60(黑)  80(红)  120(红)     ---->          20(黑)  50(黑)   80(红)  120(红)
         *         //                                               //   \\
         *       50(红)                                           45(红) 60(红)
         *        //
         *      45(红)
         */

        redBlackTree.add(48);
        /*
         *           70(黑)                                             70(黑)
         *        //       \\                                        //       \\
         *    40(红)         90(黑)                               40(红)         90(黑)
         *   //    \\       //   \\            变色             //    \\       //    \\            绕40左旋
         * 20(黑)  50(黑)  80(红)  120(红)      ---->         20(黑)  50(红)   80(红)  120(红)        ---->
         *        //  \\                                            //   \\
         *      45(红) 60(红)                                      45(黑) 60(黑)
         *        \\                                                 \\
         *        48(红)                                            48(红)
         *
         *                   70(黑)                                               50(黑)
         *               //        \\                                        //          \\
         *           50(红)          90(黑)                               40(红)            70(红)
         *          //    \\        //    \\      绕70右旋，变色          //    \\          //   \\
         *      40(红)    60(黑)  80(红)  120(红)      ---->          20(黑)   45(黑)    60(黑)  90(黑)
         *     //    \\                                                         \\            //   \\
         *  20(黑)    45(黑)                                                     48(红)     80(红)  120(红)
         *              \\
         *             48(红)
         *
         */

        Assert.assertEquals(Arrays.asList(50,40,70,20,45,60,90,48,80,120), redBlackTree.sequence());
    }


    @Test
    public void testRemove() {
        /*
         *          初始化如下一颗红黑树
         *
         *                 50(黑)
         *            //          \\
         *         40(红)          70(红)
         *        //   \\         //   \\
         *      20(黑)  45(黑)   60(黑)  90(黑)
         *      //        \\           //
         *    15(红)      48(红)      80(红)
         */
        RedBlackTree<Integer> redBlackTree = new RedBlackTree<>();
        Integer[] nums = {40,70,90,20,60,80,50,45,48,15,15};
        for (Integer num : nums) {
            redBlackTree.add(num);
        }

        // 删除不存结点
        Assert.assertFalse(redBlackTree.remove(100));

        // 删除20，转换为删除它的左孩子15
        redBlackTree.remove(20);
        /*
         *                 50(黑)
         *            //          \\
         *         40(红)          70(红)
         *        //   \\         //   \\
         *      15(黑)  45(黑)   60(黑)  90(黑)
         *                \\           //
         *                48(红)      80(红)
         */

        // 删除40，转换为删除它的直接后继45，又转换为删除45的右孩子48
        redBlackTree.remove(40);
        /*
         *                 50(黑)
         *            //          \\
         *         45(红)          70(红)
         *        //   \\         //   \\
         *      15(黑)  48(黑)   60(黑)  90(黑)
         *                             //
         *                           80(红)
         */

        // 删除15，兄弟结点48自损变红，双亲结点45变黑
        redBlackTree.remove(15);
        /*
         *                 50(黑)                               50(黑)
         *            //          \\                        //          \\
         *         45(红)          70(红)                45(黑)          70(红)
         *        //   \\         //   \\       ---->       \\         //   \\
         *      15(黑)  48(黑)   60(黑)  90(黑)              48(红)   60(黑)  90(黑)
         *                             //                                   //
         *                           80(红)                                80(红)
         */

        // 删除根节点50，转换为删除它的直接后继60
        redBlackTree.remove(50);
        /*
         *              50(黑)                             50(黑)                               60(黑)
         *           //       \\                        //       \\                          //       \\
         *        45(黑)      70(红)     绕结点90右旋   45(黑)     70(红)      绕结点70左旋    45(黑)     80(红)
         *           \\      //   \\       ---->         \\      //   \\       ---->          \\      //   \\
         *          48(红)  60(黑) 90(黑)                48(红)  60(黑) 80(黑)                 48(红)  70(黑) 90(黑)
         *                         //                                   \\
         *                       80(红)                                 90(红)
         */

        // 删除90，兄弟结点70自损变红，双亲结点80变黑
        redBlackTree.remove(90);
        /*
         *             60(黑)                            60(黑)
         *          //       \\                      //       \\
         *        45(黑)     80(红)      ---->     45(黑)     80(黑)
         *          \\      //   \\                  \\      //
         *          48(红) 70(黑) 90(黑)              48(红) 70(红)
         */

        // 删除70，70是红色叶子结点，直接删除
        redBlackTree.remove(70);
        /*
         *            60(黑)
         *         //      \\
         *       45(黑)    80(黑)
         *          \\
         *          48(红)
         */

        // 删除80
        redBlackTree.remove(80);
        /*
         *            60(黑)                           60(黑)                            48(黑)
         *         //      \\      绕结点45左旋       //      \\     绕结点60右旋        //    \\
         *       45(黑)    80(黑)     ---->        48(黑)    80(黑)     ---->        45(黑)  60(黑)
         *          \\                             //
         *          48(红)                       45(红)
         */

        Assert.assertEquals(Arrays.asList(48,45,60), redBlackTree.sequence());
    }
}
