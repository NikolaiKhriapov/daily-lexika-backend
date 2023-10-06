import {Heading, Box, Center, Flex, Stack, Badge, useColorModeValue} from '@chakra-ui/react';

export default function ReviewWordCard({reviewWordDTO, isFlipped, setIsFlipped}) {

    const handleFlip = () => setIsFlipped(!isFlipped);

    return (
        <Center py={10}>
            <Flex
                onClick={handleFlip}
                justify="center" align="center" h={'500px'} w={'400px'}
                rounded={"lg"} size={"sm"} borderWidth={'1px'} boxShadow={'2xl'}
                bg={useColorModeValue('gray.200', 'rgba(30,30,30)')}
                style={{
                    transform: isFlipped ? 'rotateY(180deg) scaleX(-1)' : 'rotateY(0deg)',
                    transition: 'transform 0.3s',
                    border: useColorModeValue(
                        (reviewWordDTO.status === 'NEW') ? '1px solid red' : '1px solid grey',
                        (reviewWordDTO.status === 'NEW') ? '1px solid red' : '1px solid rgba(70,70,70)'
                    )
                }}
            >
                <Box p={6} style={{backfaceVisibility: 'hidden'}}>
                    <Stack spacing={2} align={'center'} mb={30}>
                        {reviewWordDTO.status === 'NEW' ? (
                            <Badge colorScheme={'red'} position="absolute" top={4} right={4}
                                   border={'1px solid rgba(255, 0, 0, 0.7)'}
                            >
                                {reviewWordDTO.status}
                            </Badge>
                        ) : null}
                        <div style={{maxWidth: '360px'}}>
                            <Heading fontSize={isFlipped ? '4xl' : '7xl'} fontWeight={'normal'} fontFamily={'Roboto'}
                                     align={'center'} mb={10}>
                                {isFlipped ? reviewWordDTO.pinyin : reviewWordDTO.nameChineseSimplified}
                            </Heading>
                        </div>
                        <div style={{maxWidth: '360px'}}>
                            <Heading fontSize={isFlipped ? '3xl' : '7xl'} fontWeight={'normal'}
                                     fontFamily={'Roboto'}
                                     align={'center'}>
                                {isFlipped ? reviewWordDTO.nameEnglish : null}
                            </Heading>
                        </div>
                    </Stack>
                </Box>
            </Flex>
        </Center>
    );
}
