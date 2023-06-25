import {useState} from 'react';
import {
    Heading, Box, Center, Flex, Stack
} from '@chakra-ui/react';

export default function ReviewWordCard({reviewWord}) {
    const [isFlipped, setIsFlipped] = useState(false);

    const handleFlip = () => {
        setIsFlipped(!isFlipped);
    };

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
                onClick={handleFlip}
                style={{
                    transform: isFlipped ? 'rotateY(180deg) scaleX(-1)' : 'rotateY(0deg)',
                    transition: 'transform 0.5s',
                }}
            >
                <Box p={6} style={{backfaceVisibility: 'hidden'}}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        <Heading fontSize={isFlipped ? '4xl' : '8xl'} fontWeight={'thin'} fontFamily={'SimSun, body'}>
                            {isFlipped ? reviewWord.pinyin : reviewWord.nameChineseSimplified}
                        </Heading>
                        <Heading fontSize={isFlipped ? '4xl' : '8xl'} fontWeight={'thin'} fontFamily={'SimSun, body'}>
                            {isFlipped ? reviewWord.nameEnglish : ''}
                        </Heading>
                    </Stack>
                </Box>
            </Flex>
        </Center>
    );
}
