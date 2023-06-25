import {
    Heading, Box, Center, Flex, Stack
} from '@chakra-ui/react';

export default function ReviewWordCard({ reviewWord }) {
    return (
        <Center py={6}>
            <Flex
                justify="center"
                align="center"
                maxH={'500px'}
                minH={'500px'}
                maxW={'400px'}
                minW={'400px'}
                w={'full'}
                m={2}
                bg={'black'}
                color={'white'}
                boxShadow={'l'}
                rounded={'md'}
                overflow={'hidden'}
            >
                <Box p={6}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading
                            fontSize={'8xl'} fontWeight={'thin'} fontFamily={'SimSun, body'}
                        >
                            {reviewWord.nameChineseSimplified}
                        </Heading>
                    </Stack>
                    <Stack>

                    </Stack>
                </Box>
            </Flex>
        </Center>
    );
}
