import React from 'react';
import {Box, Flex, Stat, StatLabel, StatNumber, useColorModeValue} from '@chakra-ui/react';

export default function StatsCard({title, stat, icon}) {
    return (
        <Box>
            <Stat shadow={'2xl'} border={'1px solid'} rounded={'lg'} px={'4'} py={'5'} width="220px"
                  borderColor={useColorModeValue('gray.400', 'rgba(80,80,80)')}
                  bg={useColorModeValue('gray.100', 'rgba(40,40,40)')}
            >
                <Flex justifyContent={'left'}>
                    <Box my={'auto'} color={useColorModeValue('black', 'gray.200')} alignContent={'center'}>
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