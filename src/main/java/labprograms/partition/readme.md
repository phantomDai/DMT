# 该文档记录每一个实验对象的分区信息
由于测试之前不知道故障的分布情况，并且APT论文研究了分区数目与APT的故障检测能力的关系。经验研究的结果
表明：分区数目与APT的表现没有明显的关系。基于上述结论：我们随机挑选每一个对象的2个范畴，并将其中的选项进
行组合得到相应的分区规则。

## 实现的过程
- 首先将原始测试帧、衍生测试帧和蜕变关系整理出来
- 根据原始测试帧划分分区
- 然后依据APT选择分区
- 进而在选择的分区中选择测试用例
- 选择蜕变关系
- 生成衍生测试用例


## ACMS
分区范畴：座舱等级和航班类型。

partition | choices
:-: | :-:
partition1 | I-1a;I-2a;*
partition2 | I-1a;I-2b;*
partition3 | I-1b;I-2a;*
partition4 | I-1b;I-2b;*
partition5 | I-1c;I-2a;*
partition6 | I-1c;I-2b;*
partition7 | I-1d;I-2a;*
partition8 | I-1d;I-2b;*

## CUBS
分区范畴：套餐和通话时间。

partition | choices
:-: | :-:
partition1 | I-1a;I-3a;*
partition2 | I-1a;I-3b;*
partition3 | I-1b;I-3a;*
partition4 | I-1b;I-3b;*

## ERS
分区范畴：职称和月销售额。

partition | choices
:-: | :-:
partition1 | I-1a;I-3a;*
partition2 | I-1a;I-3b;*
partition3 | I-1a;I-3c;*
partition4 | I-1a;I-3d;*
partition5 | I-1b;I-3a;*
partition6 | I-1b;I-3b;*
partition7 | I-1b;I-3c;*
partition8 | I-1b;I-3d;*
partition9 | I-1c;I-3a;*
partition10 | I-1c;I-3b;*
partition11 | I-1c;I-3c;*
partition12 | I-1c;I-3d;*



## MOS
分区范畴：飞机模型和是否改变机组成员。

partition | choices
:-: | :-:
partition1 | I-1a;I-2a;*
partition2 | I-1a;I-2b;*
partition3 | I-1b;I-2a;*
partition4 | I-1b;I-2b;*
partition5 | I-1c;I-2a;*
partition6 | I-1c;I-2b;*
partition7 | I-1d;I-2a;*
partition8 | I-1d;I-2b;*
partition9 | I-1e;I-2a;*
partition10 | I-1e;I-2b;*
