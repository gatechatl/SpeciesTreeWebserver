#include<stdio.h>

int main (int argc, char *argv[])
{
int i,j,k,l,m;

for(i=0;i<4;i++)
{
for(j=i+1;j<=4;j++)
{
printf("%d",j);
}
for(k=1;k<=i;k++)
{
printf("%d",k);
}
printf("\n");
for(k=i;k>=1;k--)
{
printf("%d",k);
}
for(j=4;j>=i+1;j--)
{
printf("%d",j);
}

}
return 0;
}
