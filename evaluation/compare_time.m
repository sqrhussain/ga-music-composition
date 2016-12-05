function y = compare_time(path1,path2)
x = [100:100:900];
y1 = load_runtime(strcat(path1,'/population'),x);
y2 = load_runtime(strcat(path2,'/population'),x);

plot(x,y1,'b');
hold on;
plot(x,y2,'r');
%legend('Default Crossover','Music Crossover');
legend('Best Chromosome','Weighted Roulette');
hold off;
xlabel('population size');
ylabel('Runtime (msec)');
end