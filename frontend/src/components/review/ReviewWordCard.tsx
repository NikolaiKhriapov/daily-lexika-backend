import { Badge, Box, Center, Flex, Heading, Stack, useColorModeValue } from '@chakra-ui/react';
import { Status, WordDTO } from '../../types/types';

export default function ReviewWordCard({ reviewWordDTO, isFlipped, setIsFlipped }: {
  reviewWordDTO: WordDTO;
  isFlipped: boolean;
  setIsFlipped: any;
}) {
  const handleFlip = () => setIsFlipped(!isFlipped);

  const isNewStatus = reviewWordDTO.status.toString() === Status[Status.NEW];

  return (
    <Center py={10}>
      <Flex
        onClick={handleFlip}
        justify='center'
        align='center'
        h='500px'
        w='400px'
        rounded='lg'
        borderWidth='1px'
        boxShadow='2xl'
        bg={useColorModeValue('gray.200', 'rgba(30,30,30)')}
        style={{
          transform: isFlipped ? 'rotateY(180deg) scaleX(-1)' : 'rotateY(0deg)',
          transition: 'transform 0.3s',
          border: isNewStatus ? '1px solid red' : '1px solid grey',
        }}
      >
        <Box p={6} style={{ backfaceVisibility: 'hidden' }}>
          <Stack spacing={2} align='center' mb={30}>
            {isNewStatus ? (
              <Badge
                colorScheme='red'
                position='absolute'
                top={4}
                right={4}
                border='1px solid rgba(255, 0, 0, 0.7)'
              >
                {reviewWordDTO.status}
              </Badge>
            ) : null}
            <div style={{ maxWidth: '360px' }}>
              <Heading
                fontSize={isFlipped ? '4xl' : '7xl'}
                fontWeight='normal'
                fontFamily='Roboto'
                mb={10}
              >
                {isFlipped ? reviewWordDTO.pinyin : reviewWordDTO.nameChineseSimplified}
              </Heading>
            </div>
            <div style={{ maxWidth: '360px' }}>
              <Heading
                fontSize={isFlipped ? '3xl' : '7xl'}
                fontWeight='normal'
                fontFamily='Roboto'
                textAlign='center'
              >
                {isFlipped ? reviewWordDTO.nameEnglish : null}
              </Heading>
            </div>
          </Stack>
        </Box>
      </Flex>
    </Center>
  );
}
