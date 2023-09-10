import React from 'react';
import {Box, Flex, Stat, StatLabel, StatNumber, useColorModeValue} from '@chakra-ui/react';

export default function StatsCard({title, stat, icon}) {
    return (
        <Box width="220px">
            <Stat
                px={{md: 4}}
                py={'5'}
                shadow={'xl'}
                border={'1px solid'}
                borderColor={useColorModeValue('gray.800', 'gray.500')}
                rounded={'lg'}>
                <Flex justifyContent={'left'}>
                    <Box
                        my={'auto'}
                        color={useColorModeValue('black', 'gray.200')}
                        alignContent={'center'}>
                        {icon}
                    </Box>
                    <Box pl={{md: 4}}>
                        <StatNumber fontSize={'2xl'} fontWeight={'medium'}>{stat}</StatNumber>
                        <StatLabel fontWeight={'medium'} isTruncated>{title}</StatLabel>
                    </Box>
                </Flex>
            </Stat>
        </Box>
    );
}