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
partition0 | I-1a;I-2a;*
partition1 | I-1a;I-2b;*
partition2 | I-1b;I-2a;*
partition3 | I-1b;I-2b;*
partition4 | I-1c;I-2a;*
partition5 | I-1c;I-2b;*
partition6 | I-1d;I-2a;*
partition7 | I-1d;I-2b;*

## CUBS
分区范畴：套餐和通话时间。

partition | choices
:-: | :-:
partition0 | I-1a;I-3a;*
partition1 | I-1a;I-3b;*
partition2 | I-1b;I-3a;*
partition3 | I-1b;I-3b;*

## ERS
分区范畴：职称和月销售额。

partition | choices
:-: | :-:
partition0 | I-1a;I-3a;*
partition1 | I-1a;I-3b;*
partition2 | I-1a;I-3c;*
partition3 | I-1a;I-3d;*
partition4 | I-1b;I-3a;*
partition5 | I-1b;I-3b;*
partition6 | I-1b;I-3c;*
partition7 | I-1b;I-3d;*
partition8 | I-1c;I-3a;*
partition9 | I-1c;I-3b;*
partition10 | I-1c;I-3c;*
partition11 | I-1c;I-3d;*



## MOS
分区范畴：飞机模型和是否改变机组成员。

partition | choices
:-: | :-:
partition0 | I-1a;I-2a;*
partition1 | I-1a;I-2b;*
partition2 | I-1b;I-2a;*
partition3 | I-1b;I-2b;*
partition4 | I-1c;I-2a;*
partition5 | I-1c;I-2b;*
partition6 | I-1d;I-2a;*
partition7 | I-1d;I-2b;*
partition8 | I-1e;I-2a;*
partition9 | I-1e;I-2b;*

## 分区的失效率

### ACMS
partition | failure rate
:-: | :-:
partition0 | 0
partition1 | 0.286
partition2 | 0.104
partition3 | 0.023
partition4 | 0.007
partition5 | 0.025
partition6 | 0
partition7 | 0

partition | punishment
:-: | :-:
partition0 | 79
partition1 | 73
partition2 | 67
partition3 | 60
partition4 | 96
partition5 | 84
partition6 | 29
partition7 | 23

### CUBS

partition | failure rate
:-: | :-:
partition0 | 0.0278
partition1 | 0.25
partition2 | 0

partition | punishment
:-: | :-:
partition0 | 50
partition1 | 5
partition2 | 43
partition3 | 0


### ERS

partition | failure rate
:-: | :-:
partition0 | 0.246
partition1 | 0.234
partition2 | 0.206
partition3 | 0.061
partition4 | 0.213
partition5 | 0.140
partition6 | 0.125
partition7 | 0.600
partition8 | 0.190
partition9 | 0.293
partition10 | 0.240
partition11 | 0.104


partition | punishment
:-: | :-:
partition0 | 91
partition1 | 89
partition2 | 88
partition3 | 126
partition4 | 62
partition5 | 39
partition6 | 39
partition7 | 51
partition8 | 55
partition9 | 52
partition10 | 35
partition11 | 53



### MOS

partition | failure rate
:-: | :-:
partition0 | 0.080
partition1 | 0.023
partition2 | 0.083
partition3 | 0.059
partition4 | 0.127
partition5 | 0.087
partition6 | 0.157
partition7 | 0.025
partition8 | 0.159
partition9 | 0

partition | punishment
:-: | :-:
partition0 | 390
partition1 | 298
partition2 | 403
partition3 | 261
partition4 | 403
partition5 | 217
partition6 | 226
partition7 | 84
partition8 | 132
partition9 | 21
