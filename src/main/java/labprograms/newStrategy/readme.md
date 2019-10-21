# 测试策略更改
讨论之前的测试策略的实现在strategy包下，跟刘淮老师以及孙老师讨论之后，确定将一下测试技术
作为benchmarks。
- MT（random + random）
- AMT-R（RAPT + random）
- AMT-R （RAPT +　PBMR）
- AMT-M (MAPT + random)
- AMT-M (MAPT + PBMR)

除了以上ｂｅｎｃｈｍａｒｋ之外，额外实现下面的测试技术。

- MT(random + static)
- AMT-R(RAPT + static)
- AMT-M(MAPT + static)

## 实现测试技术用到的设计模式

- 策略模式：执行测试的具体步骤不同，具有的行为也不同，因此需要使用策略模式。
- 工厂模式：处理创建大量相关具体的类的细节。



