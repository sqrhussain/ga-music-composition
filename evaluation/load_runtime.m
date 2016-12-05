function y = load_runtime( path,arr )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
  y=[];
  for i=1:1:length(arr)
    name = strcat(path,num2str(arr(i)),'.stat');
    fileID = fopen(name);
    x1 = fscanf(fileID,'%s',5);
    x1 = fscanf(fileID,'%g',1);
    x2 = fscanf(fileID,'%g ',1);
    y = [y,x1];
    fclose(fileID);
  end;
end

