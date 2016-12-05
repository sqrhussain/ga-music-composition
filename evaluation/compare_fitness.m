function y = compare_fitness(path1,path2)
x = [100:100:900];
y1 = load_last_fitness(strcat(path1,'/population'),x);
y2 = load_last_fitness(strcat(path2,'/population'),x);

plot(x,y1,'b');
hold on;
plot(x,y2,'r');
legend('Best Chromosome','Weighted Roulette');
%legend('Default Crossover','Custom Crossover');
hold off;
xlabel('population size');
ylabel('fitness value');
end