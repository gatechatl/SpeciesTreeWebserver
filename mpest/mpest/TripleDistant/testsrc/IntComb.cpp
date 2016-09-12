// IntComb.cpp : Defines the entry point for the console application.
//

#include "IndexCombination.h"
#include <iostream>
#include <string>
#include <vector>

using namespace std;
using namespace stdcomb;


int main(int argc, char* argv[])
{
	CIdxComb cb;

	cb.SetSizes(5,3);


	vector<int> vsAnimal;
	vsAnimal.push_back(1);
	vsAnimal.push_back(2);
	vsAnimal.push_back(3);
	vsAnimal.push_back(4);
	vsAnimal.push_back(5);

	vector<unsigned int> vi(3);

	vi[0] = 0;
	vi[1] = 1;
	vi[2] = 2;

	cout<< vsAnimal[ vi[0] ] << " " 
		<< vsAnimal[ vi[1] ] << " " 
		<< vsAnimal[ vi[2] ] << "\n";

	int Total = 1;
	while ( cb.GetNextComb( vi ) )
	{
		// Do whatever processing you want
		
		
		cout<< vsAnimal[ vi[0] ] << " " 
			<< vsAnimal[ vi[1] ] << " " 
			<< vsAnimal[ vi[2] ] << endl; 
		
		++Total;
	}

	cout<< "\nTotal : " << Total << endl;
	//system( "pause" );

	return 0;
}
