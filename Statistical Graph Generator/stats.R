#!/usr/bin/env Rscript
library(ggplot2)
x <- scan("final_stats.txt", what="", sep="\n")
# Separate elements by one or more whitepace
z<- vector(mode="numeric", length=0)
y <- strsplit(x, "[[:space:]]+")
#ScatterPlot4WordLength()
pfunc1<-function (){
for (row in y){
row=as.numeric(unlist(row))
z=c(z,row)
}
counter=1:length(z)
plot(counter,z)

}
getStats<-function (){
  numOfConv=0
  numOfTurn=0
  numOfWord=0
  for (row in y){
    row=as.numeric(unlist(row))
    z=c(z,row)
    numOfConv=numOfConv+1
    numOfTurn=numOfTurn+length(row)
    numOfWord=numOfWord+sum(row)
  }
  numTurnPerConv=numOfTurn/numOfConv
  numWordPerConv=numOfWord/numOfConv
  numWordPerTurn=numOfWord/numOfTurn
  res<-c(numOfConv,numOfTurn,numOfWord,numTurnPerConv,numWordPerConv,numWordPerTurn)
  title<-c("numOfConv","numOfTurn","numOfWord","avg numTurnPerConv","avg numWordPerConv","avg numWordPerTurn")
  statsDf<-data.frame(title,res)
  print(statsDf)
}
#WordCountAsFunctionOfTurn
pfunc2<-function (){
for (row in y){
row=as.numeric(unlist(row))
z=c(z,row)
}
z.cum=cumsum(z)
counter=1:length(z)
fapp<-loess(z.cum~counter)
jpeg(file="WdAsFncOfTurn.jpeg")
plot(counter,z.cum,main="total word count as function of number of turns",xlab="turn",ylab="total word count")
lines(predict(fapp),col="red",lwd=2)
dev.off()
}
#graph the counts of conversation for number of utterance 1:n
pfunc3<-function(){
turnNumber=vector(mode="double",length=0)
for (row in y){
row=as.numeric(unlist(row))
#z=c(z,row)
turnNumber=c(turnNumber,length(row))
}
plot<-ggplot() + aes(turnNumber,y=log(..count..))+ geom_histogram(binwidth=15, colour="#35b6c4", fill="#35b6c4",alpha=.5)+ geom_vline(aes(xintercept=mean(turnNumber)),color="red", linetype="solid", size=1)+labs(x="Number of turns", y = "ln(Number of conversations)")+stat_bin(binwidth=15, geom="text", colour="black", size=3.5,
           aes(label=round(log(..count..),2)), position=position_stack(vjust=0.9))+scale_x_continuous(breaks = seq(0, 350, 15))
          #  +scale_y_continuous(breaks = seq(0, 200,10))
#jpeg(file="numofTurn.jpeg")
pdf(file="numofTurn.pdf")
print(plot)
dev.off()
}
doGraphPPL<-function(){
  x <- scan("pp_stats.txt", what="", sep="\n")
  z<- vector(mode="double", length=0)

  for(row in x){
    row =as.double(unlist(row))
    z=c(z,row)
  }
plot<-ggplot() + aes(z,y=log(..count..))+ geom_histogram(binwidth=1, colour="#35b6c4", fill="#35b6c4",alpha=.5)+ geom_vline(aes(xintercept=mean(z)),color="red", linetype="solid", size=.5)+labs(x="Number of people", y = "ln(Number of conversations)")+stat_bin(binwidth=1, geom="text", colour="black", size=2,
           aes(label=round(log(..count..),2)), position=position_stack(vjust=0.95))+scale_x_continuous(breaks = seq(0, 40, 2))
          #  +scale_x_continuous(breaks = seq(0, 350, 15))+scale_y_continuous(breaks = seq(0, 200,10))
#jpeg(file="numofTurn.jpeg")
pdf(file="ppl.pdf")
print(plot)
dev.off()
}

 #pfunc2()
pfunc3()
getStats()
doGraphPPL()
