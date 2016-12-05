function y = load_evolution(prefix,files)
  n = length(files);
  y = [];
  for i = 1:n
    %strcat(prefix,files(i))
    y = [y;load(strcat(prefix,int2str(files(i))))];
  end
  mesh(y);
  xlabel('Generation');
  ylabel('Population size');
  zlabel('Fitness value');
  
end