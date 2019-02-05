#include <iostream>

int main(int argc, char *argv[]) {
    if(argc != 3) {
        std::cout << "Usage: ./ path/to/raw/results/file.xml path/to/sarif/results/file.sarif\n" ;
        return 1;
    }
    std::cout << "Translating from " << argv[1] << "\n\t to " << argv[2] << std::endl;

    // TODO: add translation code.

    return 0;
}
