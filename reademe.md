# DMT项目介绍

## 测试步骤
- 筛选4个实验室程序的变异体，得到失效率小于0.1的变异体；
- 识别MRs；
- 建立测试用例、MRs与分区的映射关系；
- 实现测试策略：DMT、MT、MT-SRTsum

注意：每一个测试策略重复实验50次。接下来，对项目中的每一个模块的内容进行详细地介绍。

## ACMS、CUBS、ERS和MOS
主要存贮各个测试对象的源程序与相应的变异体。

## constant
主要记录一些常用的静态变量。

## gethardkilledmutants
利用反射技术来筛选每一个测试对象难以检测的变异体。

## method
记录了每一个测试对象的测试方法名字

## mr
从xml中读取测试帧以及相应的蜕变关系

## mutants
- mutant：主要属性是变异体的路径
- mutantSet：记录每一个测试对象的所有变异体

## partition
- ACMS、CUBS、ERS和MOS存放了文件名为1-n的文件夹，每一个文件夹中有若干的文件，文件名字为原始测试帧、衍生测试帧和相应的蜕变关系
- DeleteDir：清空指定的测试对象每一个分区中的文件
- GenerateDir：为每一个测试对象自动生成分区文件
- partition4X：将测试用例依据分区规则划分到对象的分区之中
- statisticsMr：统计每一个测试对象分区中的文件数目，验证测试用例划分的正确性

## testcase
 - TestCase4X：每一个实验对象对应的测试用例
 - TestSuite4X：每一个实验对象的测试用例集合
 
## testingresults
- X：每一个实验对象所有变异体的杀死信息
- GetSpecifiedFaultRate：依据指定的失效率自动返回符合条件的变异体名称

## testpool
存放每一个测试对象10000个测试用例

## util

- DeleteNullLines：删除文件中的空行
- WriteTestCases：将测试用例写到json文件中
- WriteTestingResult：将测试结果写到文件中

# test
该目录是项目的测试文件主目录

## ACMS、CUBS、ERS和MOS
存放相应的变异体的测试脚本

## XTestAll
是一个测试用例集，包含了ACMS、CUBS、ERS和MOS文件夹中的所有测试用例，方便执行

# 该项目的代码量：14090